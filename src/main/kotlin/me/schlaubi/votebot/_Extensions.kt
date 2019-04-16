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

import cc.hawkbot.regnum.client.command.Group
import cc.hawkbot.regnum.client.command.GroupBuilder
import cc.hawkbot.regnum.client.command.permission.GroupPermissions

private val VOTE_GROUP = GroupBuilder()
    .setDescription("All vote comamnds")
    .setName("Vote")
    .setPermissions(GroupPermissions(public = true, node = "vote"))
    .build()

val Group.Companion.VOTE
    get() = VOTE_GROUP