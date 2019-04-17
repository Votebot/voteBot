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
import cc.hawkbot.regnum.client.util.Misc
import me.schlaubi.votebot.VOTE
import me.schlaubi.votebot.commands.VoteBotCommand
import me.schlaubi.votebot.core.VoteBot

class VoteCommand(
    bot: VoteBot
) : VoteBotCommand(
    bot,
    Group.VOTE,
    "Vote",
    arrayOf("vote", "addvote", "v"),
    CommandPermissions(public = true, node = "vote"),
    "<messageId> <index>",
    "568018624518815744 1",
    "Adds your vote to a poll"
) {
    override fun execute(args: Arguments, context: Context) {
        if (args.size < 2) {
            return context.sendHelp().queue()
        }

        if (!Misc.isNumeric(args[0])) {
            return context.sendMessage(EmbedUtil.error(
                context.translate("phrases.invalid.number.title"),
                context.translate("phrases.invalid.number.description")
            )).queue()
        }
        val vote = bot.voteCache.getVoteByMessage(args[0].toLong(), context.guild.idLong) ?: return context.sendMessage(
            EmbedUtil.error(
                context.translate("vote.invalid.title"),
                context.translate("vote.invalid.description")
            )
        ).queue()
        if (!Misc.isNumeric(args[1])) {
            return context.sendMessage(EmbedUtil.error(
                context.translate("phrases.invalid.number.title"),
                context.translate("phrases.invalid.number.description")
            )).queue()
        }
        val index = args[1].toInt() - 1
        try {
            vote.controller.addVote(context.author, index).thenRun {
                context.sendMessage(EmbedUtil.success(
                    context.translate("vote.success.title"),
                    context.translate("vote.success.description")
                        .format(args[1])
                )).queue()
            }
        } catch (e: IllegalArgumentException) {
            if (e.message == "This option does not exist") {
                context.sendMessage(EmbedUtil.error(
                    context.translate("vote.invalid.option.title"),
                    context.translate("vote.invalid.option.description")
                )).queue()
            } else {
                context.sendMessage(EmbedUtil.warn(
                    context.translate("vote.same.title"),
                    context.translate("vote.same.description")
                )).queue()
            }
        } catch (e: IllegalStateException) {
            @Suppress("SpellCheckingInspection")
            if (vote.maximumChanges > 1) {
                context.sendMessage(EmbedUtil.error(
                    context.translate("vote.toomany.changes.title"),
                    context.translate("vote.toomany.changes.description")
                )).queue()
            } else {
                context.sendMessage(EmbedUtil.error(
                    context.translate("vote.toomany.votes.title"),
                    context.translate("vote.toomany.votes.description")
                )).queue()
            }
        }
    }
}