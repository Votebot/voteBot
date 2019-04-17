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
import me.schlaubi.votebot.checkPermissions
import me.schlaubi.votebot.commands.VoteBotCommand
import me.schlaubi.votebot.core.VoteBot

class QuickCreateCommand(
    bot: VoteBot
) : VoteBotCommand(
    bot,
    Group.VOTE,
    "Quick create",
    arrayOf("quickcreate", "quick-create", "qc"),
    CommandPermissions(public = true, node = "quickcreate"),
    "heading|answer1|answer2|...",
    "Is that bot cool?|yes|no",
    "Creates a vote using default settings"
) {

    override fun execute(args: Arguments, context: Context) {
        val arguments = args.string<String>().split("\\|".toRegex())
        if (arguments.size < 3) {
            return context.sendHelp().queue()
        }
        if (bot.voteCache.getVoteByMember(context.member) != null) {
            return context.sendMessage(
                EmbedUtil
                    .error(
                        context.translate("vote.error.already.title"),
                        context.translate("vote.error.already.description")
                    )
            ).queue()
        }
        checkPermissions(context) {
            val heading = arguments[0]
            val options = arguments.subList(1, arguments.size)
            if (options.size > 10) {
                return@checkPermissions context.sendMessage(
                    EmbedUtil.error(
                        context.translate("vote.limit.title"),
                        context.translate("vote.limit.description")
                    )
                ).queue()
            }
            val user = bot.userCache[context.author]
            bot.voteCache.initializeVote(
                context.channel,
                context.member,
                heading,
                options,
                user.defaultMaximumVotes,
                user.defaultMaximumChanges
            )
        }
    }
}