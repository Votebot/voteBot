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

class Config(path: String) : GenericConfig(path) {

    companion object {
        const val SENTRY_DSN = "sentry.dsn"
        const val GAMES = "games"
        const val COMMAND_PREFIX = "command.language"
        const val COMMAND_OWNERS = "command.owners"
        const val CASSANDRA_KEYSPACE = "cassandra.keyspace"
        const val CASSANDRA_USERNAME = "cassandra.username"
        const val CASSANDRA_PASSWORD = "cassandra.password"
        const val CASSANDRA_CONTACT_POINTS = "cassandra.contact_points"
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