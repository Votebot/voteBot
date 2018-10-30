package me.schlaubi.votebot.core.entities;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.*;
import lombok.Getter;
import me.schlaubi.votebot.VoteBot;
import me.schlaubi.votebot.core.cache.Cache;
import me.schlaubi.votebot.io.database.SnowflakeDatabaseEntity;

@Getter
@Table(name = "guilds")
public class Guild extends SnowflakeDatabaseEntity<Guild> {

    @Column
    private String prefix = "v!";

    public Guild(Long entityId) {
        super(Guild.class, VoteBot.getInstance().getDatabaseConnection(), "[GUILD]", entityId);
        save();
    }

    @SuppressWarnings("unused")
    public Guild() {
        super(Guild.class, VoteBot.getInstance().getDatabaseConnection(), "[GUILD]", 0);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        save();
    }

    private void save() {
        VoteBot.getInstance().getGuildCache().update(this);
    }

    @Accessor
    public interface GuildProvider extends Cache.DatabaseEntityAccessor<Guild> {
        @Override
        @Query("SELECT * FROM guilds WHERE id = :id")
        Result<Guild> get(@Param("id") long id);
    }
}
