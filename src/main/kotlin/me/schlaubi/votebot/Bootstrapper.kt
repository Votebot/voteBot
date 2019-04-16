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

package me.schlaubi.votebot

import cc.hawkbot.regnum.client.Feature
import cc.hawkbot.regnum.client.RegnumBuilder
import cc.hawkbot.regnum.client.command.translation.AutoLoadingLanguageManager
import cc.hawkbot.regnum.client.command.translation.Language
import cc.hawkbot.regnum.client.command.translation.defaults.PropertyLanguage
import cc.hawkbot.regnum.client.config.CassandraConfig
import cc.hawkbot.regnum.client.config.CommandConfig
import cc.hawkbot.regnum.client.config.GameAnimatorConfig
import cc.hawkbot.regnum.client.config.ServerConfig
import cc.hawkbot.regnum.client.core.discord.GameAnimator
import cc.hawkbot.regnum.sentry.SentryAppender
import cc.hawkbot.regnum.sentry.SentryClient
import cc.hawkbot.regnum.util.logging.Logger
import me.schlaubi.votebot.core.impl.VoteBotImpl
import me.schlaubi.votebot.io.config.Config
import org.apache.commons.cli.BasicParser
import org.apache.commons.cli.Options
import org.apache.commons.cli.Option
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.ConfigurationSource
import org.apache.logging.log4j.core.config.Configurator
import java.util.*

fun main(args: Array<String>) {

    val options = Options()
        .addOption(
            Option("D", "debug", false, "Enables debug mode")
        )
        .addOption(
            Option("LL", "log-level", true, "Defines the logging level")
        )
        .addOption(
            Option("NS", "no-sentry", true, "Disable sentry logging")
        )

    val cmd = BasicParser().parse(options, args)

    Configurator.setRootLevel(Level.toLevel(cmd.getOptionValue("LL"), Level.INFO))
    Configurator.initialize(ClassLoader.getSystemClassLoader(), ConfigurationSource(ClassLoader.getSystemResourceAsStream("log4j2.xml")))

    val log = Logger.getLogger()
    log.info("[Launcher] Booting up VoteBot!")

    val config = Config("config/bot.yml")
    val sentry = SentryClient(config.get(Config.SENTRY_DSN))
    SentryAppender.injectSentry(sentry)
    log.info("[Launcher] Initialized Sentry!")

    val debug = cmd.hasOption("D")
    val regnumBuilder = RegnumBuilder()
    regnumBuilder.disableFeatures(Feature.MESSAGE_CACHE)
    val games = config.get<List<String>>(Config.GAMES)
        .map { GameAnimator.Game.compile(it) }
    regnumBuilder.gameAnimatorConfig = GameAnimatorConfig(games.toMutableList())
    val commandConfig = CommandConfig(config.get<String>(Config.COMMAND_PREFIX), AutoLoadingLanguageManager("locales", Language.defaultLanguage(PropertyLanguage(
        Locale.forLanguageTag("en-US"), "locales/en-US.properties"))))
    commandConfig.addBotOwners(config.get<List<Long>>(Config.COMMAND_OWNERS))
    regnumBuilder.commandConfig = commandConfig
    regnumBuilder.cassandraConfig = CassandraConfig(
        config.get(Config.CASSANDRA_KEYSPACE),
        config.get(Config.CASSANDRA_USERNAME),
        config.get(Config.CASSANDRA_PASSWORD),
        config.get(Config.CASSANDRA_CONTACT_POINTS)
    )
        .addDefaultDatabases("CREATE TABLE IF NOT EXISTS user(" +
                "id BIGINT," +
                "default_maximum_votes INT," +
                "default_maximum_changes INT," +
                "default_vote_length TEXT," +
                "PRIMARY KEY (id)" +
                ");")
        .addDefaultDatabases("create table if not exists votes " +
                "(" +
                "guild BIGINT," +
                "messages MAP<BIGINT, BIGINT>," +
                "author BIGINT," +
                "heading TEXT," +
                "options LIST<TEXT>," +
                "answers MAP<BIGINT, frozen<LIST<INT>>>," +
                "emote_mapping MAP<TEXT, INT>," +
                "vote_counts MAP<BIGINT, INT>," +
                "maximum_votes int," +
                "maximum_changes int," +
                "created_at timestamp," +
                "primary key (guild, author)" +
                ");"
        )
        .addDefaultDatabases("CREATE INDEX IF NOT EXISTS messageKey ON votes(KEYS(messages));")
        .addDefaultDatabases("create table IF NOT EXISTS guilds" +
                "(" +
                "id bigint primary key," +
                "use_custom_emotes boolean" +
                ");" +
                "")
    regnumBuilder.serverConfig = ServerConfig(config.get(Config.REGNUM_HOST), config.get(Config.REGNUM_TOKEN))
    log.info("[Launcher] Initializing Regnum!")
    VoteBotImpl(regnumBuilder.build())
}