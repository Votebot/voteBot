/*
 * Votebot - A feature-rich bot to create votes on Discord guilds.
 *
 * Copyright (C) 2019  Michael Rittmeister & Yannick Seeger & Daniel Scherf
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

package wtf.votebot.bot

import com.configcat.ConfigCatClient
import com.google.common.flogger.FluentLogger
import com.orbitz.consul.Consul
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException
import wtf.votebot.bot.core.VoteBot
import wtf.votebot.bot.exceptions.StartupError
import wtf.votebot.bot.io.EnvConfig
import wtf.votebot.bot.io.RemoteConfig
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

private val log = FluentLogger.forEnclosingClass()
private val options = Options()
    .addOption(
        Option.builder("D")
            .longOpt("dev")
            .desc("Enables developer mode")
            .build()
    )
    .addOption(
        Option.builder("CPI")
            .longOpt("config-poll-interval")
            .desc("Defines the ConfigCat poll interval")
            .hasArg()
            .type(Int::class.java)
            .build()
    )
    .addOption(
        Option.builder("H")
            .longOpt("help")
            .desc("Displays a list of all CLI options")
            .build()
    )

fun main(args: Array<String>) {

    // CLI
    val cli: CommandLine = parseCliOptions(args)

    if (cli.hasOption("H")) {
        sendHelp()
        exitProcess(0)
    }
    val devEnabled = cli.hasOption("dev")
    if (devEnabled) {
        return launchDevMode()
    }

    launchNormally(cli)
}

/**
 * Launches the bot in developer mode.
 */
fun launchDevMode() {
    log.atInfo().log("Launching in developer mode!")
    if (Files.exists(Path.of(".env"))) {
        throw StartupError("Please place a .env file in the bots root directory when starting in dev mode!")
    }
    val config = EnvConfig()
    VoteBot(config, null)
}

/**
 * Launches the bot in production mode.
 */
@Suppress("SpellCheckingInspection")
fun launchNormally(cli: CommandLine) {
    val consul = Consul.builder().build()
    val configCatKey = consul.keyValueClient().getValueAsString("configcat_key")
        .orElseThrow {
            StartupError(
                "ConfigCat not found or empty. Please make sure you added a ConfigCat key" +
                        " to Consul or enable developer mode"
            )
        }
    val configCatPollInterval = if (cli.hasOption("CPI")) {
        cli.getParsedOptionValue("CPI") as Int
    } else {
        RemoteConfig.DEFAULT_POLL_INTERVAL
    }

    val config = retrieveConfig(configCatPollInterval, configCatKey)
    VoteBot(config, consul)
}

/**
 * Creates a [ConfigCatClient] using the specified [configCatPollInterval] and [configCatKey].
 */
private fun retrieveConfig(configCatPollInterval: Int, configCatKey: String): RemoteConfig {
    return RemoteConfig(configCatPollInterval, configCatKey)
}

/**
 * Parses the specified [args] into an [CommandLine] object.
 */
private fun parseCliOptions(args: Array<String>): CommandLine {
    val cli: CommandLine
    try {
        cli = DefaultParser().parse(options, args)
    } catch (e: ParseException) {
        log.atSevere().withCause(e).log("Could not parse CLI options")
        sendHelp()
        exitProcess(1)
    }

    return cli
}

/**
 * Sends a formatted message of all CLI options.
 */
private fun sendHelp() = HelpFormatter().printHelp("[options]", options)
