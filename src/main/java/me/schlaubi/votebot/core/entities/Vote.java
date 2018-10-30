package me.schlaubi.votebot.core.entities;

import com.datastax.driver.mapping.annotations.Table;
import lombok.Getter;
import me.schlaubi.votebot.io.database.Cassandra;
import me.schlaubi.votebot.io.database.DatabaseEntity;

@Getter
@Table(name = "votes")
public class Vote extends DatabaseEntity<Vote> {

    public Vote(Class<Vote> clazz, Cassandra cassandra, String logPrefix, long entityId) {
        super(clazz, cassandra, logPrefix);
    }
}
