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

package me.schlaubi.votebot.commands

import cc.hawkbot.regnum.client.command.Command
import cc.hawkbot.regnum.client.command.Group
import cc.hawkbot.regnum.client.command.context.Context
import cc.hawkbot.regnum.client.command.permission.IPermissions
import cc.hawkbot.regnum.client.util.EmbedUtil
import cc.hawkbot.regnum.client.util.Misc
import me.schlaubi.votebot.core.VoteBot
import me.schlaubi.votebot.entities.Vote

/**
 * Extension of [Command] adding [bot] field.
 * @see Command
 */
abstract class VoteBotCommand(
    protected val bot: VoteBot,
    group: Group,
    displayName: String,
    aliases: Array<String>,
    permissions: IPermissions,
    usage: String = "",
    exampleUsage: String = "",
    description: String
) : Command(group, displayName, aliases, permissions, usage, exampleUsage, description) {

    /**
     * Checks if vote exists in [context] and passes it into [action]
     */
    protected fun hasVote(context: Context, action: (Vote) -> Unit) {
        val vote = bot.voteCache.getVoteByUser(context.author, context.guild) ?: return context.sendMessage(
            EmbedUtil.error(
                context.translate("vote.notexist.title"),
                context.translate("vote.notexist.description")
            )
        ).queue()
        action(vote)
    }

    protected fun hasNoVote(context: Context, action: () -> Unit) {
        if (bot.voteCache.getVoteByUser(context.author, context.guild) != null) {
            return context.sendMessage(
                EmbedUtil.error(
                    context.translate("vote.error.already.title"),
                    context.translate("vote.error.already.description")
                )
            ).queue()
        }
        action()
    }

    protected fun validateInt(context: Context, argument: String, action: (Int) -> Unit) =
        validateNumber(context, argument, { argument.toInt() }, action)

    protected fun validateLong(context: Context, argument: String, action: (Long) -> Unit) =
        validateNumber(context, argument, { argument.toLong() }, action)

    @Suppress("MemberVisibilityCanBePrivate")
    protected fun <T> validateNumber(
        context: Context,
        argument: String,
        parser: (argument: String) -> T,
        action: (T) -> Unit
    ) {
        if (!Misc.isNumeric(argument)) {
            return context.sendMessage(
                EmbedUtil.error(
                    context.translate("phrases.invalid.number.title"),
                    context.translate("phrases.invalid.number.description")
                )
            ).queue()
        }
        action(parser(argument))
    }
}

