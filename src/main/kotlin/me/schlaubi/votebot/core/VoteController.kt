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

import me.schlaubi.votebot.entities.Vote
import net.dv8tion.jda.api.entities.User
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class VoteController(
    private val vote: Vote
) {
    fun updateMessages(): CompletableFuture<Void> {
        val future = CompletableFuture<Void>()
        vote.messages
            .thenApply { it.keys }
            .thenApply { it.map { message -> message.editMessage(vote.renderVote().build()).submit() } }
            .thenAccept {
                CompletableFuture.allOf(*it.toTypedArray())
                    .thenRun {
                        future.complete(null)
                    }
            }
        return future
    }

    fun addVote(user: User, answer: Int): CompletionStage<Void> {
        // Check if option does exist
        if (answer > vote.options.size) {
            throw IllegalStateException("This option does not exist")
        }
        val maximumVotes = vote.maximumVotes
        val userId = user.idLong
        if (maximumVotes == 1) {
            // Check if user is allowed to vote
            val maximumChanges = vote.maximumChanges
            var voteCount = vote.voteCounts[userId] ?: 0
            if (voteCount == maximumChanges) {
                return CompletableFuture.completedStage(null)
            }
            // Register vote
            vote.answers[userId] = mutableListOf(answer)
            voteCount++
            vote.voteCounts[userId] = voteCount
        } else {
            // Check if user has already storage to much.
            var userVotes = vote.answers[userId]
            if (userVotes != null) {
                if (userVotes.size == maximumVotes) {
                    user.openPrivateChannel().queue {
                        it.sendMessage("You cannot vote more than `$maximumVotes` times!").queue()
                    }
                    return CompletableFuture.completedStage(null)
                }
                userVotes.add(answer)
            } else {
                userVotes = mutableListOf(answer)
            }
            vote.answers[userId] = userVotes
        }
        return CompletableFuture.allOf(vote.saveAsync().toCompletableFuture(), updateMessages())
    }
}