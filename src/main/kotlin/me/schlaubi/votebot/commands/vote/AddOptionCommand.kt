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

class AddOptionCommand(
    bot: VoteBot
) : VoteBotCommand(
    bot,
    Group.VOTE,
    "Add option",
    arrayOf("addoption", "add-option", "ao"),
    CommandPermissions(public = true, node = "addoption"),
    "<option>",
    "Can you please tell me the question again I was sleeping?",
    "Adds another option to the vote"
) {
    override fun execute(args: Arguments, context: Context) {
        if (args.isEmpty()) {
            return context.sendHelp().queue()
        }
        val vote = bot.voteCache.getVoteByUser(context.author, context.guild) ?: return context.sendMessage(
            EmbedUtil.error(
                context.translate("vote.notexist.title"),
                context.translate("vote.notexist.description")
            )
        ).queue()
        val option = args.string<String>()
        try {
            vote.controller.addOption(option)
        } catch (e: IllegalStateException) {
            return context.sendMessage(
                EmbedUtil.error(
                    context.translate("vote.limit.title"),
                    context.translate("vote.limit.description")
                )
            ).queue()
        }
        context.sendMessage(
            EmbedUtil.success(
                context.translate("command.addoption.success.title"),
                context.translate("command.addoption.success.description")
                    .format(option)
            )
        ).queue()
    }
}