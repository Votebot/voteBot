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

import cc.hawkbot.regnum.client.Regnum
import cc.hawkbot.regnum.client.command.Group
import cc.hawkbot.regnum.client.command.context.Arguments
import cc.hawkbot.regnum.client.command.context.Context
import cc.hawkbot.regnum.client.command.permission.CommandPermissions
import cc.hawkbot.regnum.client.interaction.SetupMessage
import cc.hawkbot.regnum.client.interaction.SetupMessageBuilder
import cc.hawkbot.regnum.client.util.EmbedUtil
import cc.hawkbot.regnum.client.util.Misc
import me.schlaubi.votebot.VOTE
import me.schlaubi.votebot.commands.VoteBotCommand
import me.schlaubi.votebot.core.VoteBot
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import java.util.concurrent.TimeUnit

class CreateCommand(bot: VoteBot) : VoteBotCommand(
    bot,
    Group.VOTE,
    "Create",
    arrayOf("create", "make", "new", "n", "c"),
    CommandPermissions(public = true, node = "create"),
    description = "Creates a vote with individual settings"
) {
    override fun execute(args: Arguments, context: Context) {
        if (true) {
            return context.sendMessage(
                "**IMPORTANT**: To create votes you can still use the `v!qc` command!\n" +
                "Unfortunately because of massive bugs we had to disable that command" +
                    "We hope to be able to bring it back soon. Thank you for you patience")
                .queue()
        }
        hasNoVote(context) {
            context.sendMessage(
                EmbedUtil.info(
                    context.translate("vote.creation.step1.info.title"),
                    context.translate("vote.creation.step1.info.description")
                )
            ).queue {
                val builder = SetupBuilder(bot)
                builder.authorizedUsers = listOf(context.author)
                builder.message = it
                builder.timeout = 30
                builder.timeUnit = TimeUnit.SECONDS
                builder.build()
            }
        }
    }
}

private class Setup(
    private val bot: VoteBot,
    regnum: Regnum,
    message: Message,
    author: User,
    timeout: Long,
    timeunit: TimeUnit,
    neededPermissions: List<Permission>,
    removeReaction: Boolean,
    disableMessageListening: Boolean
) : SetupMessage(
    regnum,
    message,
    listOf(author),
    timeout,
    timeunit,
    neededPermissions,
    removeReaction,
    disableMessageListening
) {

    private val author = message.guild.getMember(author)
    private lateinit var channel: TextChannel
    private lateinit var heading: String
    private val options = mutableListOf<String>()
    private var maximumChanges: Int = 1
    private var maximumVotes: Int = 1

    init {
        waitForInteraction()
    }

    override fun handleStep(event: GuildMessageReceivedEvent, step: Int) {
        event.message.delete().queue()
        when (step) {
            1 -> {
                val mentionedChannels = event.message.mentionedChannels
                if (mentionedChannels.isEmpty()) {
                    return editMessage(
                        EmbedUtil.error(
                            translate("vote.creation.step1.error.nochannel.title"),
                            translate("vote.creation.step1.error.nochannel.description")
                        )
                    ).queue().also { retry() }
                }
                val channel = mentionedChannels[0]
                if (!event.guild.selfMember.hasPermission(
                        channel,
                        Permission.MESSAGE_EMBED_LINKS,
                        Permission.MESSAGE_MANAGE,
                        Permission.MESSAGE_ADD_REACTION,
                        Permission.MESSAGE_ATTACH_FILES
                    )
                ) {
                    return editMessage(
                        EmbedUtil.error(
                            translate("vote.error.permissions.title"),
                            translate("vote.error.permissions.description")
                        )
                    ).queue().also { retry() }
                }
                this.channel = channel
                editMessage(
                    EmbedUtil.info(
                        translate("vote.creation.step2.info.title"),
                        translate("vote.creation.step2.info.description")
                    )
                ).queue()
                next()
            }
            2 -> {
                val heading = event.message.contentDisplay
                if (heading.length > MessageEmbed.TITLE_MAX_LENGTH) {
                    return editMessage(
                        EmbedUtil.error(
                            translate("vote.creation.step2.length.title"),
                            translate("vote.creation.step2.length.description")
                                .format(MessageEmbed.TITLE_MAX_LENGTH)
                        )
                    ).queue().also { retry() }
                }
                this.heading = heading
                editMessage(
                    EmbedUtil.info(
                        translate("vote.creation.step3.info.title"),
                        translate("vote.creation.step3.info.description")
                    )
                ).queue()
                next()
            }
            3 -> {
                val option = event.message.contentDisplay
                if (option == "end") {
                    return editMessage(
                        EmbedUtil.info(
                            translate("vote.creation.step4.info.title"),
                            translate("vote.creation.step4.info.description")
                        )
                    ).queue().also { next() }
                }
                if (options.size > 10) {
                    return editMessage(
                        EmbedUtil.error(
                            translate("vote.creation.step3.error.count.title"),
                            translate("vote.creation.step3.error.count.description")
                        )
                    ).queue().also { retry() }
                }
                options += option
                editMessage(
                    EmbedUtil.info(
                        translate("vote.creation.step3.added.title"),
                        translate("vote.creation.step3.added.description")
                            .format(option)
                    )
                ).queue()
                retry()
            }
            4 -> {
                val answer = event.message.contentRaw
                if (answer == "auto") {
                    val user = bot.userCache[event.author]
                    maximumChanges = user.defaultMaximumChanges
                    return finish()
                }
                if (answer == "-1") {
                    return editMessage(
                        EmbedUtil.info(
                            translate("vote.creation.step5.info.title"),
                            translate("vote.creation.step5.info.description")
                        )
                    ).queue().also { next() }
                }
                if (!Misc.isNumeric(answer)) {
                    return editMessage(
                        EmbedUtil.error(
                            translate("phrases.invalid.number.title"),
                            translate("phrases.invalid.number.description")
                        )
                    ).queue().also { retry() }
                }
                maximumChanges = answer.toInt()
                finish()
            }
            5 -> {
                val answer = event.message.contentRaw
                if (answer == "auto") {
                    val user = bot.userCache[event.author]
                    maximumVotes = user.defaultMaximumVotes
                    return finish()
                }
                if (!Misc.isNumeric(answer)) {
                    return editMessage(
                        EmbedUtil.error(
                            translate("phrases.invalid.number.title"),
                            translate("phrases.invalid.number.description")
                        )
                    ).queue().also { retry() }
                }
                maximumChanges = answer.toInt()
                finish()
            }
        }
    }

    override fun finish() {
        super.finish()
        bot.voteCache.initializeVote(
            channel,
            author,
            heading,
            options,
            maximumVotes,
            maximumChanges
        )
        editMessage(
            EmbedUtil.success(
                translate("vote.creation.thanks.title"),
                translate("vote.creation.thanks.description")
            )
        ).queue()
    }

    /* It's empty I know but I have to override it */
    override fun handleStep(event: GuildMessageReactionAddEvent, step: Int) = Unit
}

private class SetupBuilder(
    private val bot: VoteBot
) : SetupMessageBuilder<Setup, SetupBuilder>(bot.regnum) {

    override fun build(): Setup {
        return Setup(
            bot,
            regnum,
            message,
            authorizedUsers[0],
            timeout,
            timeUnit,
            listOf(Permission.MESSAGE_MANAGE, Permission.MESSAGE_EMBED_LINKS),
            removeReaction = false,
            disableMessageListening = false
        )
    }
}