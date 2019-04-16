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

package me.schlaubi.votebot.entities;

import cc.hawkbot.regnum.client.entities.cache.CacheableCassandraEntity;
import cc.hawkbot.regnum.client.entities.cache.CassandraCache;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import com.datastax.driver.mapping.annotations.Table;
import org.jetbrains.annotations.NotNull;

/**
 * Entity class which represents VoteBot user settings.
 */
@Table(name = "user")
public class VoteUser extends CacheableCassandraEntity<VoteUser> {

    /**
     * Maximal amount of storage a user can add to a vote.
     */
    @Column(name = "default_maximum_votes")
    private int defaultMaximumVotes = 1;
    /**
     * Maximal amount of times the user can change its vote.
     */
    @Column(name = "default_maximum_changes")
    private int defaultMaximumChanges = 3;
    /**
     * Time till a vote gets closed automatically.E
     */
    @Column(name = "default_vote_length")
    private String defaultVoteLength = "-1";

    @CassandraCache.Constructor
    public VoteUser(long id) {
        super(id);
    }

    public VoteUser() {
        super(-1);
    }

    public int getDefaultMaximumVotes() {
        return defaultMaximumVotes;
    }

    public void setDefaultMaximumVotes(int defaultMaximumVotes) {
        this.defaultMaximumVotes = defaultMaximumVotes;
    }

    public int getDefaultMaximumChanges() {
        return defaultMaximumChanges;
    }

    public void setDefaultMaximumChanges(int defaultMaximumChanges) {
        this.defaultMaximumChanges = defaultMaximumChanges;
    }

    public String getDefaultVoteLength() {
        return defaultVoteLength;
    }

    public void setDefaultVoteLength(String defaultVoteLength) {
        this.defaultVoteLength = defaultVoteLength;
    }

    @com.datastax.driver.mapping.annotations.Accessor
    public interface Accessor extends CacheableCassandraEntity.Accessor<VoteUser> {

        @Query("SELECT * FROM user WHERE id = :id")
        @NotNull
        @Override
        Result<VoteUser> get(@Param("id") long id);
    }
}
