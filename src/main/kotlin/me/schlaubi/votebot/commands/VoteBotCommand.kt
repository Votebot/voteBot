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
import me.schlaubi.votebot.core.VoteBot
import net.dv8tion.jda.api.Permission

abstract class VoteBotCommand(
    protected val bot: VoteBot,
    group: Group,
    displayName: String,
    aliases: Array<String>,
    permissions: IPermissions,
    usage: String,
    exampleUsage: String,
    description: String
): Command(group, displayName, aliases, permissions, usage, exampleUsage, description)