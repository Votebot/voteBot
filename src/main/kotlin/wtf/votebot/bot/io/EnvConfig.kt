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

package wtf.votebot.bot.io

import io.github.cdimascio.dotenv.dotenv
import wtf.votebot.bot.exceptions.StartupError

/**
 * Implementation of [Config] that reads the config from an `.env` file.
 */
class EnvConfig : Config {

    private val dotenv = dotenv()

    override val devEnabled: Boolean = true
    override val discordToken: String
        get() = dotenv[DISCORD_TOKEN] ?: notFound(DISCORD_TOKEN)

    @Suppress("SameParameterValue") // There will be more options
    private fun notFound(option: String): Nothing = throw StartupError(
        "Could not find $option in .env file." +
                "Please make sure to include all options from the example"
    )

    companion object {
        /**
         * .env file key for the Discord API token.
         */
        const val DISCORD_TOKEN = "DISCORD_TOKEN"
    }
}
