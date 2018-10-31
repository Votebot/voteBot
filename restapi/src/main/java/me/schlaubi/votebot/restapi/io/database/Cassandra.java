package me.schlaubi.votebot.restapi.io.database;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AuthenticationException;
import com.datastax.driver.mapping.MappingManager;
import lombok.Getter;

import java.io.Closeable;

@Getter
public class Cassandra implements Closeable {

    private final Cluster cluster;
    private final Session session;
    private final MappingManager mappingManager;

    public Cassandra(String[] contactPoints, String username, String password, String keyspace) throws IllegalStateException, AuthenticationException {
            this.cluster = new Cluster.Builder()
                    .addContactPoints(contactPoints)
                    .withCredentials(username, password)
                    .withoutJMXReporting()
                    .build();
            this.session = cluster.connect(keyspace);
            this.mappingManager = new MappingManager(session);
    }

    @Override
    public void close() {
        cluster.close();
    }
}
