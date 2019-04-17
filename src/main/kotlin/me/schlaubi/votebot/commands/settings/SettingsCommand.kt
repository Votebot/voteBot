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

package me.schlaubi.votebot.commands.settings

import cc.hawkbot.regnum.client.command.Command
import cc.hawkbot.regnum.client.command.Group
import cc.hawkbot.regnum.client.command.context.Arguments
import cc.hawkbot.regnum.client.command.context.Context
import cc.hawkbot.regnum.client.command.permission.CommandPermissions
import cc.hawkbot.regnum.client.util.EmbedUtil
import cc.hawkbot.regnum.client.util.Misc
import me.schlaubi.votebot.core.VoteBot

class SettingsCommand(
    private val bot: VoteBot
) : Command(
    Group.SETTINGS,
    "Settings",
    arrayOf("settings", "preferences", "pref"),
    CommandPermissions(public = true, node = "settings"),
    "<maxVotes/maxChanges/length> <newValue>",
    "defaultMaxVotes 2",
    "Changes your own settings for storage"
) {
    override fun execute(args: Arguments, context: Context) {
        val user = bot.userCache[context.author]
        if (args.size == 0) {
            return context.sendMessage(
                EmbedUtil.info(
                    context.translate("command.settings.current.title"),
                    context.translate("command.settings.current.description")
                        .format(user.defaultMaximumVotes, user.defaultMaximumChanges, user.defaultVoteLength)
                )
            ).queue()
        }
        when (args[0].toLowerCase()) {
            "maxvotes" -> {
                setting(args, {
                    @Suppress("SpellCheckingInspection")
                    context.sendMessage(
                        EmbedUtil.info(
                            context.translate("command.settings.current.maxvotes.title"),
                            context.translate("command.settings.current.maxvotes.description")
                                .format(user.defaultMaximumVotes)
                        )
                    ).queue()
                }, {
                    checkSame(context, it, user.defaultMaximumVotes) {
                        if (user.defaultMaximumChanges > 1) {
                            return@checkSame context.sendMessage("You have to disable changes!").queue()
                        }
                        if (!Misc.isNumeric(it)) {
                            return@checkSame context.sendMessage(
                                EmbedUtil.error(
                                    context.translate("phrases.invalid.number.title"),
                                    context.translate("phrases.invalid.number.description")
                                )
                            ).queue()
                        }
                        user.defaultMaximumVotes = it.toInt()
                        user.saveAsync().thenRun {
                            @Suppress("SpellCheckingInspection")
                            context.sendMessage(
                                EmbedUtil.success(
                                    context.translate("command.settings.success.maxvotes.title"),
                                    context.translate("command.settings.success.maxvotes.description")
                                        .format(it)
                                )
                            ).queue()
                        }
                    }
                })
            }
            "maxchanges" -> {
                setting(args, {
                    @Suppress("SpellCheckingInspection")
                    context.sendMessage(
                        EmbedUtil.info(
                            context.translate("command.settings.current.maxchanges.title"),
                            context.translate("command.settings.current.maxchanges.description")
                                .format(user.defaultMaximumChanges)
                        )
                    ).queue()
                }, {
                    checkSame(context, it, user.defaultMaximumChanges) {
                        if (user.defaultMaximumVotes > 1) {
                            return@checkSame context.sendMessage("You have to disable multiple votes!").queue()
                        }
                        if (!Misc.isNumeric(it)) {
                            return@checkSame context.sendMessage(
                                EmbedUtil.error(
                                    context.translate("phrases.invalid.number.title"),
                                    context.translate("phrases.invalid.number.description")
                                )
                            ).queue()
                        }
                        user.defaultMaximumChanges = it.toInt()
                        user.saveAsync().thenRun {
                            @Suppress("SpellCheckingInspection")
                            context.sendMessage(
                                EmbedUtil.success(
                                    context.translate("command.settings.success.maxchanges.title"),
                                    context.translate("command.settings.success.maxchanges.description")
                                        .format(it)
                                )
                            ).queue()
                        }
                    }
                })
            }
            "length" -> {
                setting(args, {
                    val time = user.defaultVoteLength
                    if (time == "-1") {
                        return@setting context.sendMessage(
                            EmbedUtil.info(
                                context.translate("command.settings.current.length.title"),
                                context.translate("command.settings.current.permanent.description")
                                    .format(time)
                            )
                        ).queue()
                    }
                    @Suppress("SpellCheckingInspection")
                    context.sendMessage(
                        EmbedUtil.info(
                            context.translate("command.settings.current.length.title"),
                            context.translate("command.settings.current.length.description")
                                .format(time)
                        )
                    ).queue()
                }, {
                    checkSame(context, it, user.defaultVoteLength) {
                        if (Misc.parseDate(it) == null) {
                            return@checkSame context.sendMessage(
                                EmbedUtil.error(
                                    context.translate("phrases.invalid.date.title"),
                                    context.translate("phrases.invalid.date.description")
                                )
                            ).queue()
                        }
                        user.defaultVoteLength = it
                        user.saveAsync().thenRun {
                            @Suppress("SpellCheckingInspection")
                            context.sendMessage(
                                EmbedUtil.success(
                                    context.translate("command.settings.success.length.title"),
                                    context.translate("command.settings.success.length.description")
                                        .format(it)
                                )
                            ).queue()
                        }
                    }
                })
            }
            else -> context.sendMessage(
                EmbedUtil.error(
                    context.translate("command.settings.invalid.title"),
                    context.translate("command.settings.invalid.description")
                )
            ).queue()
        }
    }

    private fun setting(args: Arguments, display: () -> Unit, change: (newValue: String) -> Unit) {
        if (args.size < 2) {
            display()
        } else {
            change(args[1])
        }
    }

    private fun checkSame(context: Context, newValue: String, oldValue: Any, action: () -> Unit) {
        if (newValue == oldValue) {
            return context.sendMessage(
                EmbedUtil.error(
                    context.translate("command.settings.same.title"),
                    context.translate("command.settings.same.description")
                )
            ).queue()
        }
        action()
    }
}