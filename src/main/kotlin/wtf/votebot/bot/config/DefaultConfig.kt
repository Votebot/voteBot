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

package wtf.votebot.bot.config

/**
 * Default [Config] implementation with default data.
 */
class DefaultConfig : Config {

    override var environment: String = Environment.PRODUCTION.key
    override var sentryDSN: String? = null
    override var discordToken: String? = null
    override var httpPort: String = "3245"
    override var vaultAddress: String = "http://127.0.0.1:8200"
    override var vaultToken: String? = null
    override var vaultPath: String = "votebot/"
}
