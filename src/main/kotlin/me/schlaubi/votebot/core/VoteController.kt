/*
 * VoteBot - A unique Discord bot for surveys
 *
 * Copyright (C) 2019  Michael Rittmeister
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package me.schlaubi.votebot.core

import cc.hawkbot.regnum.client.util.Colors
import cc.hawkbot.regnum.client.util.Misc
import cc.hawkbot.regnum.client.util.SafeMessage
import cc.hawkbot.regnum.client.util.TranslationUtil
import me.schlaubi.votebot.core.graphics.PieChart
import me.schlaubi.votebot.core.graphics.PieTile
import me.schlaubi.votebot.entities.Vote
import me.schlaubi.votebot.getEmotesForGuild
import me.schlaubi.votebot.util.Utils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.internal.utils.Helpers
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.function.Consumer
import java.util.function.Function

class VoteController(
    private val vote: Vote
) {

    init {
        vote.initialize()
    }

    private fun updateMessages(): CompletableFuture<Set<Message>> {
        return vote.messages
            // Map to messages only
            .thenApplyAsync(Function<Map<Message, TextChannel>, Set<Message>> { t -> t.keys }, VoteCache.THREAD_POOL)
            .toCompletableFuture()
            .also {
                it.thenAcceptAsync(Consumer<Set<Message>> { messages ->
                    val embed = renderVote().build()
                    messages.forEach { message ->
                        message.editMessage(embed).queue()
                    }
                }, VoteCache.THREAD_POOL)
            }
    }

    fun addVote(user: User, answer: Int): CompletionStage<Void> {
        // Check if option does exist
        if (answer >= vote.options.size) {
            throw IllegalArgumentException("This option does not exist")
        }

        // Check if user has already voted for the same option
        val maximumVotes = vote.maximumVotes
        val userId = user.idLong
        val userVotes = vote.answers[userId]
        if (userVotes != null && answer in userVotes) {
            throw IllegalArgumentException("User already voted for that")
        }
        // Check for "opinion changeable" mode or "multiple opinion" mode
        if (maximumVotes == 1) {
            // Check if user is allowed to vote
            val maximumChanges = vote.maximumChanges
            val voteCount = vote.voteCounts[userId] ?: 0
            if (voteCount >= maximumChanges) {
                throw IllegalStateException("To may votes")
            }
            // Register vote
            vote.answers[userId] = mutableListOf(answer)
            vote.voteCounts[userId] = voteCount + 1
        } else {
            // Check if user has already storage to much.
            val votes = vote.answers[userId] ?: mutableListOf()
            if (votes.size == maximumVotes) {
                throw IllegalStateException("You have votes to much!")
            }
            votes.add(answer)
            // Save the result if needed
            if (votes.size == 1) {
                vote.answers[userId] = votes
            }
        }
        // Update all messages and save vote -> combine it into one future
        return CompletableFuture.allOf(vote.saveAsync().toCompletableFuture(), updateMessages())
    }

    fun setHeading(heading: String) {
        vote.heading = heading
        updateMessages()
        vote.saveAsync()
    }

    fun addOption(option: String) {
        if (vote.options.size == 10) {
            throw IllegalStateException("Vote cannot have more than 10 options!")
        }
        // Get all emotes
        val emotes = vote.guild.getEmotesForGuild(vote.cache.bot)
        // Filter out used emotes
        emotes.removeAll { it in vote.emoteMapping.keys }
        // Assign emote to option
        val emote = emotes.first()
        vote.emoteMapping[emote] = vote.options.size
        // Register option
        vote.options.add(option)
        vote.saveAsync()
        updateMessages().thenAccept {
            it.forEach { message ->
                Misc.addReaction(emote, message).queue()
            }
        }
    }

    fun removeOption(optionIndex: Int) {
        if (optionIndex >= vote.options.size) {
            throw IllegalStateException("Unknown option!")
        }
        // Unregister emote
        val emote = Misc.getKeyOrNullByValue(vote.emoteMapping, optionIndex)!!
        vote.emoteMapping.remove(emote)

        // Gift users another vote if the've voted for that option
        val users = vote.answers.filterValues { optionIndex in it }
        users.forEach { (user, answers) ->
            answers.remove(optionIndex)
            vote.answers[user] = answers
            if (vote.maximumVotes > 1) {
                vote.voteCounts[user] = (vote.voteCounts[user] ?: 1) - 1
            }
        }
        // Rearrange votes
        if (optionIndex != vote.options.size - 1) {
            for (i in optionIndex + 1 until vote.options.size) {
                vote.answers.forEach { (user, votes) ->
                    if (i in votes) {
                        votes -= i
                        votes += i - 1
                        vote.answers[user] = votes
                    }
                }
            }
        }
        // Rearrange emotes
        vote.emoteMapping.filterValues { it > optionIndex }.forEach { (emote, index) ->
            vote.emoteMapping[emote] = index - 1
        }
        vote.options.removeAt(optionIndex)
        vote.saveAsync()
        updateMessages().thenAccept {
            it.forEach { message ->
                Utils.removeReactionByIdentifier(emote, message).queue()
            }
        }
    }

    fun deleteVote() {
        vote.deleteAsync()
        if (vote.answers.isEmpty()) {
            vote.messages.thenApply { it.keys }
                .thenAccept { fallbackEdit(it) }
            throw IllegalStateException("No votes")
        }
        // Only generate chart when it's possible
        if (vote.options.size == vote.options.distinct().size) {
            // Generate chart
            val chart = generateChart()
            // Loop through all channels
            sendChartMessages(chart)
        } else {
            // Fallback edit if chart could not be generated because of dupes
            vote.messages.thenApply { it.keys }
                .thenAccept {
                    fallbackEdit(it)
                }
            throw IllegalArgumentException("Dupes")
        }
    }

    private fun sendChartMessages(chart: PieChart) {
        vote.messages.thenAccept {
            val messages = it.keys
            // Filter out channels with no permissions
            val channels = it.asSequence().distinct().filter { channel ->
                val textChannel = channel.value
                textChannel.guild.selfMember.hasPermission(
                    textChannel,
                    Permission.MESSAGE_ATTACH_FILES
                )
            }
            if (channels.any()) {
                // Send chart to all applicable channels
                channels.forEach { entry ->
                    entry.value.sendFile(chart.toInputStream(), "chart.png").queue()
                    entry.key.delete().queue()
                }
            } else {
                // Fallback handling if no suitable channel could be found
                fallbackEdit(messages)
            }
        }
    }

    private fun generateChart(): PieChart {
        val tiles = mutableListOf<PieTile>()
        val totalVotes = calculateVoteCount()
        vote.options.forEach {
            val votes = calculateOptionAnswers(vote.options.indexOf(it))
            val percentage = votes.toDouble() / totalVotes.toDouble()
            tiles += PieTile(it, percentage)
        }
        return PieChart(vote.heading, tiles.toTypedArray())
    }

    private fun calculateOptionAnswers(optionIndex: Int) =
        vote.answers.values.filter { chosen -> chosen.contains(optionIndex) }.count()


    private fun calculateVoteCount(): Int {
        // When there is only one vote per user we don't need to add all voteCounts of the users
        return if (vote.maximumVotes <= 1) {
            vote.answers.size
        } else {
            vote.answers.values
                .stream()
                // Sum user based votes
                .mapToInt { it.size }
                // Sum all user based counts
                .sum()
        }
    }

    // just edit the footer
    private fun fallbackEdit(messages: MutableSet<Message>) {
        messages.forEach {
            it.editMessage(renderVote().setFooter("Vote closed!", null).build()).queue()
            it.clearReactions().queue()
        }
    }

    fun renderVote(): EmbedBuilder {
        val member = vote.author
        val guild = member.guild
        val user = member.user
        val builder = EmbedBuilder()
            .setColor(Colors.BLURLPLE)
            .setAuthor(user.name, "https://votevot.hawkbot.cc/view/", user.avatarUrl)
            .setFooter("React or use the vote command to vote", user.jda.selfUser.avatarUrl)
            .setTimestamp(vote.createdAt)
            .setTitle(vote.heading)
        val answers = StringBuilder()
        val options = vote.options
        for (i in 0 until options.size) {
            val option = options[i]
            val votes = if (vote.answers.isEmpty()) 0 else calculateOptionAnswers(i)
            val emoteRaw = Misc.getKeyOrNullByValue<String, Int>(vote.emoteMapping, i)!!
            answers.append("**").append(i + 1).append("**").append(". ").append(
                if (Helpers.isNumeric(emoteRaw)) Utils.mentionEmote(emoteRaw, guild) else emoteRaw
            )
                .append(" - ").append(option).append(": `").append(votes).append('`')
                .append(System.lineSeparator())
        }
        builder.setDescription(answers)
        return builder
    }

    fun translate(key: String) = TranslationUtil.translate(vote.cache.bot.regnum, key, vote.authorId)

}