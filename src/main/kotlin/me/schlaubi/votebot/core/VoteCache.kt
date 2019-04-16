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
import me.schlaubi.votebot.entities.Vote
import me.schlaubi.votebot.util.Utils
import net.dv8tion.jda.api.entities.*
import java.util.concurrent.CompletableFuture

interface VoteCache {

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
            if (guild.usesCustomEmotes()) {
                emotes.addAll(0, channel.guild.emotes.map { it.id })
            }
            val emoteMapping = mutableMapOf<String, Int>()
            val iterator = emotes.iterator()
            for (i in 0..options.size) {
                emoteMapping[iterator.next()] = i
            }

            var futures = arrayOf<CompletableFuture<Void>>()
            emoteMapping.keys.forEach { emote ->
                futures += Misc.addReaction(emote, it).submit()
            }
            println("ADD REACT")
            CompletableFuture.allOf(*futures).thenRun {
                println("FUTURE")
                val vote = createVote(
                    author,
                    it,
                    heading,
                    options,
                    emoteMapping,
                    maximumVotes,
                    maximumChanges
                )
                println("EDIT")
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