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

@Table(name = "guilds")
public class VoteGuild extends CacheableCassandraEntity<VoteGuild> {

    @Column(name = "use_custom_emotes")
    private boolean useCustomEmotes = false;

    @CassandraCache.Constructor
    public VoteGuild(long id) {
        super(id);
    }

    public VoteGuild() {
        super(-1);
    }

    public boolean usesCustomEmotes() {
        return useCustomEmotes;
    }

    public void setUseCustomEmotes(boolean useCustomEmotes) {
        this.useCustomEmotes = useCustomEmotes;
    }

    @com.datastax.driver.mapping.annotations.Accessor
    public interface Accessor extends CacheableCassandraEntity.Accessor<VoteGuild> {
        @Query("SELECT * FROM guilds WHERE id = :id")
        @NotNull
        @Override
        Result<VoteGuild> get(@Param("id") long id);
    }
}
