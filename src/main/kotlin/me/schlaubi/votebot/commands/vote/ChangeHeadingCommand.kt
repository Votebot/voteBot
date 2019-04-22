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
import me.schlaubi.votebot.VOTE
import me.schlaubi.votebot.commands.VoteBotCommand
import me.schlaubi.votebot.core.VoteBot

class ChangeHeadingCommand(
    bot: VoteBot
) : VoteBotCommand(
    bot,
    Group.VOTE,
    "Change heading",
    arrayOf("changeheading", "heading", "ch"),
    CommandPermissions(public = true, node = "changeheading"),
    "<newHeading>",
    "Is this a useless heading?",
    "Updates the heading of a Vote"
) {
    override fun execute(args: Arguments, context: Context) {
        if (args.isEmpty()) {
            return context.sendHelp().queue()
        }
        hasVote(context) {
            val heading = args.string<String>()
            it.controller.setHeading(heading)
            context.sendMessage(
                EmbedUtil.success(
                    context.translate("command.changeheading.success.title"),
                    context.translate("command.changeheading.success.description")
                        .format(heading)
                )
            ).queue()
        }
    }
}