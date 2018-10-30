package me.schlaubi.votebot.core.entities;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.*;
import lombok.Getter;
import me.schlaubi.votebot.VoteBot;
import me.schlaubi.votebot.core.cache.Cache;
import me.schlaubi.votebot.core.command.permission.UserPermissions;
import me.schlaubi.votebot.io.database.SnowflakeDatabaseEntity;

import java.util.Locale;

@Getter
@Table(name = "users")
public class User extends SnowflakeDatabaseEntity<User> {

    @Column(name = "language")
    private String languageTag = "en-US";

    public User(Long entityId) {
        super(User.class, VoteBot.getInstance().getDatabaseConnection(), "[USER]", entityId);
        save();
    }

    @SuppressWarnings("unused")
    public User() {
        super(User.class, VoteBot.getInstance().getDatabaseConnection(), "[USER]", 0);
    }

    @Transient
    public Locale getLocale() {
        return Locale.forLanguageTag(languageTag);
    }

    public void setLocale(Locale locale) {
        this.languageTag = locale.toLanguageTag();
        save();
    }

    @Transient
    public UserPermissions getPermissions() {
        return new UserPermissions(entityId);
    }

    private void save() {
        VoteBot.getInstance().getUserCache().update(this);
    }

    @Accessor
    public interface UserProvider extends Cache.DatabaseEntityAccessor<User> {
        @Override
        @Query("SELECT * FROM users WHERE id= :id")
        Result<User> get(@Param("id") long id);
    }
}
