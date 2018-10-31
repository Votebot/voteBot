package me.schlaubi.votebot.io.database;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.AuthenticationException;
import com.datastax.driver.mapping.MappingManager;
import lombok.Getter;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Cassandra implements Closeable {

    private final Cluster cluster;
    private final Session session;
    private final MappingManager mappingManager;
    private final List<Database> defaults;

    public Cassandra(String[] contactPoints, String username, String password, String keyspace) throws IllegalStateException, AuthenticationException {
            this.cluster = new Cluster.Builder()
                    .addContactPoints(contactPoints)
                    .withCredentials(username, password)
                    .build();
            this.session = cluster.connect(keyspace);
            this.defaults = new ArrayList<>();
            addDefaults(defaults);
            defaults.forEach(database -> {
                BoundStatement boundStatement = session.prepare(database.createStatement()).bind();
                session.execute(boundStatement);
            });
            this.mappingManager = new MappingManager(session);

    }

    private void addDefaults(List<Database> defaults) {
        defaults.add(() -> "CREATE TABLE IF NOT EXISTS votes (\n" +
                "  guild_id bigint,\n" +
                "  author_id bigint,\n" +
                "  answers map<text, int>,\n" +
                "  emotes map<text, text>,\n" +
                "  heading text,\n" +
                "  messages map<bigint, bigint>,\n" +
                "  user_votes map<bigint, text>,\n" +
                "  vote_counts map<bigint, int>,\n" +
                "  PRIMARY KEY (guild_id, author_id)\n" +
                ")");
        defaults.add(() -> "CREATE TABLE IF NOT EXISTS users(\n" +
                "  id bigint PRIMARY KEY,\n" +
                "  \"language\" text\n" +
                ")");
        defaults.add(() -> "CREATE TABLE IF NOT EXISTS guilds (\n" +
                "  id bigint PRIMARY KEY,\n" +
                "  prefix text\n" +
                ")");
    }

    @Override
    public void close() {
        cluster.close();
    }

    private interface Database {
        String createStatement();
    }
}
