package me.schlaubi.votebot.restapi.entities;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import me.schlaubi.votebot.restapi.io.database.Cassandra;

public class EntityProvider {

    private final Vote.VoteAccessor voteAccessor;
    private final Session session;

    public EntityProvider(Cassandra cassandra, Class<? extends Vote.VoteAccessor> accessorClazz) {
        voteAccessor = cassandra.getMappingManager().createAccessor(accessorClazz);
        session = cassandra.getSession();
    }

    public Vote getVoteByMessage(long messageId) {
        return voteAccessor.getVoteByMessage(messageId).one();
    }

    public Vote getVoteByMember(long guildId, long authorId) {
        return voteAccessor.getVoteByMember(guildId, authorId).one();
    }

    public Integer getGuildCount() {
        BoundStatement statement = session.prepare("SELECT COUNT(*) FROM guilds").bind();
        ResultSet rs = session.execute(statement);
        return (int) rs.one().getLong("count");

    }

    public Integer getUserCount() {
        BoundStatement statement = session.prepare("SELECT COUNT(*) FROM users").bind();
        ResultSet rs = session.execute(statement);
        return (int) rs.one().getLong("count");
    }

    public Integer getVoteCount() {
        BoundStatement statement = session.prepare("SELECT COUNT(*) FROM votes").bind();
        ResultSet rs = session.execute(statement);
        return (int) rs.one().getLong("count");
    }

}
