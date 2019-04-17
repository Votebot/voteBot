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

package me.schlaubi.votebot.io.config

import cc.hawkbot.regnum.io.config.GenericConfig

/**
 * Configuration class for VoteBot.
 * @param path path of config
 * @constructor Initializes config
 */
class Config(path: String) : GenericConfig(path) {

    companion object {
        // DSN for centralized logging with sentry
        const val SENTRY_DSN = "sentry.dsn"
        // Array of games for game animator
        const val GAMES = "games"
        // Prefix for commands
        const val COMMAND_PREFIX = "command.prefix"
        // Array of user ids wo owns owner permissions
        const val COMMAND_OWNERS = "command.owners"
        // Cassandra login data
        const val CASSANDRA_KEYSPACE = "cassandra.keyspace"
        const val CASSANDRA_USERNAME = "cassandra.username"
        const val CASSANDRA_PASSWORD = "cassandra.password"
        const val CASSANDRA_CONTACT_POINTS = "cassandra.contact_points"
        // Regnum server data
        const val REGNUM_HOST = "regnum.host"
        const val REGNUM_TOKEN = "regnum.token"
    }

    init {
        load()
    }

    override fun defaults() {
        applyDefault(SENTRY_DSN, "sentry-dsn")
        applyDefault(GAMES, listOf("game"))
        applyDefault(COMMAND_PREFIX, "v!")
        applyDefault(COMMAND_OWNERS, listOf(416902379598774273L))
        applyDefault(CASSANDRA_KEYSPACE, "votebot")
        applyDefault(CASSANDRA_USERNAME, "cassandra")
        applyDefault(CASSANDRA_PASSWORD, "")
        applyDefault(CASSANDRA_CONTACT_POINTS, listOf("127.0.0.1"))
        applyDefault(REGNUM_HOST, "localhost:3001")
        applyDefault(REGNUM_TOKEN, "SUPER-SECRET-TOKEN")
    }
}