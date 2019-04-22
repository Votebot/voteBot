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

package me.schlaubi.votebot.core.impl

import me.schlaubi.votebot.core.VoteBot
import me.schlaubi.votebot.core.VoteCache
import me.schlaubi.votebot.entities.Vote
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.User

class VoteCacheImpl(
    override val bot: VoteBot
) : VoteCache {

    override val storage: MutableList<Vote>
    private val accessor = bot.regnum.cassandra.mappingManager.createAccessor(Vote.Accessor::class.java)

    init {
        storage = accessor.all.all()
        // Filter out votes that are not managed by one of this nodes and inject cache
        storage.filterNot { bot.regnum.discord.shardManager.getGuildById(it.guildId) == null }.map { it.cache = this }
    }

    override fun getVoteByMessage(messageId: Long, guildId: Long) =
        storage.firstOrNull { it.messagesIds.containsKey(messageId) && it.guildId == guildId }


    override fun getVoteByUser(user: User, guild: Guild) =
        storage.firstOrNull { it.authorId == user.idLong && it.guildId == guild.idLong }

    override fun updateVote(vote: Vote) {
        if (vote in storage) {
            deleteVote(vote)
        }
        storage.add(vote)
    }

    override fun deleteVote(vote: Vote) = storage.remove(vote).run { Unit }

    override fun createVote(
        author: Member,
        message: Message,
        heading: String,
        options: List<String>,
        emoteMapping: Map<String, Int>,
        maximumVotes: Int,
        maximumChanges: Int
    ): Vote {
        val guild = author.guild.idLong
        val messageIds = mapOf(Pair(message.idLong, message.channel.idLong))
        val answers = mutableMapOf<Long, List<Int>>()
        val voteCounts = mutableMapOf<Long, Int>()
        val vote = Vote(
            guild,
            messageIds,
            author.idLong,
            heading,
            options,
            answers,
            emoteMapping,
            voteCounts,
            maximumVotes,
            maximumChanges
        )
        vote.cache = this
        vote.saveAsync()
        return vote
    }
}