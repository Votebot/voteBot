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

package me.schlaubi.votebot.commands.general

import cc.hawkbot.regnum.client.command.Command
import cc.hawkbot.regnum.client.command.Group
import cc.hawkbot.regnum.client.command.context.Arguments
import cc.hawkbot.regnum.client.command.context.Context
import cc.hawkbot.regnum.client.command.permission.CommandPermissions
import cc.hawkbot.regnum.client.util.EmbedUtil

class InfoCommand : Command(
    Group.GENERAL,
    "Info",
    arrayOf("info", "about"),
    CommandPermissions(public = true, node = "info"),
    description = "Displays generic information about the bot"
) {

    override fun execute(args: Arguments, context: Context) {
        context.sendMessage(
            EmbedUtil.info(
                context.translate("command.info.title"),
                context.translate("command.info.description")
            )
                .setFooter(context.translate("command.info.footer"), context.me.user.avatarUrl)
                .addField(context.translate("command.info.author"), "[Schlaubi#1337](https://rittmeister.in)", true)
                .addField(context.translate("command.info.community"), "[CrazyPilz#0117](https://crazypilz.ga)", true)
                .addField(context.translate("command.info.designer"), "[Rxsto#1337](https://rxsto.me)", true)
                // Don't judge me for this. It's all IntelliJ's fault
                .addField(
                    context.translate("command.info.source"),
                    "[github.com](https://github.com/DRSchlaubi/votebot)",
                    true
                )
                .addField(
                    context.translate("command.info.translate"),
                    "[i18n.hawkbot.cc](https://i18n.hawkbot.cc)",
                    true
                )
                .addField(
                    context.translate("command.info.support"),
                    "[discord.gg/j9RCgsn](https://discord.gg/j9RCgsn)",
                    true
                )
                .addField(
                    context.translate("command.info.donate"),
                    "[paypal.me/schlaubiboy](https://paypal.me/Schlaubiboy)",
                    true
                )
        ).queue()
    }
}