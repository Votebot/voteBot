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

import cc.hawkbot.regnum.client.util.*
import cc.hawkbot.regnum.util.DefaultThreadFactory
import me.schlaubi.votebot.entities.Vote
import me.schlaubi.votebot.util.Utils
import net.dv8tion.jda.api.entities.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors

interface VoteCache {

    companion object {
        @JvmStatic
        val THREAD_POOL = Executors.newCachedThreadPool(DefaultThreadFactory("VoteCache"))!!
    }

    val bot: VoteBot

    val storage: List<Vote>

    fun getVoteByMessage(messageId: Long, guildId: Long): Vote?

    fun getVoteByUser(user: User, guild: Guild): Vote?

    fun getVoteByMember(member: Member): Vote? {
        return getVoteByUser(member.user, member.guild)
    }

    fun getVoteByMessage(message: Message): Vote? {
        return getVoteByMessage(message.idLong, message.guild.idLong)
    }

    fun updateVote(vote: Vote)

    fun deleteVote(vote: Vote)

    fun initializeVote(
        channel: TextChannel,
        author: Member,
        heading: String,
        options: List<String>,
        maximumVotes: Int,
        maximumChanges: Int
    ) {
        SafeMessage.sendMessage(
            EmbedUtil.info(
                TranslationUtil.translate(bot.regnum, "vote.loading.title", author.idLong),
                TranslationUtil.translate(bot.regnum, "vote.loading.description", author.idLong)
                    .format(Emotes.LOADING)
            )
            , channel
        ).queue {
            val guild = bot.guildCache[channel.guild]
            val emotes = Utils.EMOTES.toMutableList()
            // It's time to shuffle
            emotes.shuffle()
            // Add custom emotes if enabled
            if (guild.usesCustomEmotes()) {
                val customEmotes = channel.guild.emotes.map { it.id }.toMutableList()
                customEmotes.shuffle()
                emotes.addAll(0, customEmotes)
            }
            val emoteMapping = mutableMapOf<String, Int>()
            val iterator = emotes.iterator()
            // Randomly map emotes to options
            for (i in 0 until options.size) {
                emoteMapping[iterator.next()] = i
            }

            // Add all reactions to the message
            var futures = arrayOf<CompletableFuture<Void>>()
            emoteMapping.keys.forEach { emote ->
                futures += Misc.addReaction(emote, it).submit()
            }
            // Wait till all reactions got added and create vote
            CompletableFuture.allOf(*futures).thenRun {
                val vote = createVote(
                    author,
                    it,
                    heading,
                    options,
                    emoteMapping,
                    maximumVotes,
                    maximumChanges
                )
                it.editMessage(vote.renderVote().build()).queue()
            }
        }
    }

    fun createVote(
        author: Member,
        message: Message,
        heading: String,
        options: List<String>,
        emoteMapping: Map<String, Int>,
        maximumVotes: Int,
        maximumChanges: Int
    ): Vote

}