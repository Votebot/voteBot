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

package me.schlaubi.votebot.commands.vote

import cc.hawkbot.regnum.client.command.Group
import cc.hawkbot.regnum.client.command.context.Arguments
import cc.hawkbot.regnum.client.command.context.Context
import cc.hawkbot.regnum.client.command.permission.CommandPermissions
import cc.hawkbot.regnum.client.util.EmbedUtil
import cc.hawkbot.regnum.client.util.Emotes
import cc.hawkbot.regnum.client.util.Misc
import me.schlaubi.votebot.VOTE
import me.schlaubi.votebot.checkPermissions
import me.schlaubi.votebot.commands.VoteBotCommand
import me.schlaubi.votebot.core.VoteBot
import java.util.concurrent.CompletableFuture

class StatusCommand(bot: VoteBot): VoteBotCommand(
    bot,
    Group.VOTE,
    "Info",
    arrayOf("status", "i", "current"),
    CommandPermissions(public = true, node = "info"),
    "[-new]",
    "-new",
    "Displays the status of the current vote. The `-new` parameter indicates that you can also vote on this message"
) {
    override fun execute(args: Arguments, context: Context) {
        // Verify that this member owns a vote
        val vote = bot.voteCache.getVoteByMember(context.member) ?: return context.sendMessage(
            EmbedUtil.error(
                context.translate("vote.notexist.title"),
                context.translate("vote.notexist.description")
            )
        ).queue()
        // Verify that this channels is suitable for vote messages (perm-check)
        checkPermissions(context) {
            // Render embed
            val embed = vote.renderVote()
            // Check for new
            if (!args.isEmpty() && args[0] == "-new") {
                // Generate new vote messages
                context.sendMessage(
                    EmbedUtil.info(
                        context.translate("vote.loading.title"),
                        context.translate("vote.loading.description")
                            .format(Emotes.LOADING)
                    )
                ).queue {
                    // Add reactions
                    val futures = mutableListOf<CompletableFuture<Void>>()
                    vote.emoteMapping.keys.forEach { emote ->
                        futures += Misc.addReaction(emote, it).submit()
                    }
                    // Register messages
                    val messageIds = vote.messagesIds.toMutableMap()
                    messageIds[it.idLong] = it.channel.idLong
                    vote.messagesIds = messageIds
                    // Save and edit message
                    vote.saveAsync().thenRun {
                        it.editMessage(vote.renderVote().build()).queue()
                    }
                }
            } else {
                // Clarify that this is just a static message
                embed.setFooter("Please do not vote on this message!", null)
                context.sendMessage(embed).queue()
            }
        }
    }
}