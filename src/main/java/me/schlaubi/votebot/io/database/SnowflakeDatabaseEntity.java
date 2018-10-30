package me.schlaubi.votebot.io.database;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Transient;
import lombok.Getter;
import net.dv8tion.jda.core.entities.ISnowflake;

@Getter
public class SnowflakeDatabaseEntity<T> extends DatabaseEntity<T> implements ISnowflake {

    @Getter
    @PartitionKey
    @Column(name = "id")
    private long entityId;

    public SnowflakeDatabaseEntity(Class<T> clazz, Cassandra cassandra, String logPrefix, long entityId) {
        super(clazz, cassandra, logPrefix);
        this.entityId = entityId;
    }

    @Override
    public String toString() {
        return String.valueOf(entityId);
    }

    @Override
    @Transient
    public long getIdLong() {
        return entityId;
    }
}
