package me.schlaubi.votebot.restapi.entities;

import me.schlaubi.votebot.restapi.io.database.Cassandra;

public class EntityProvider {

    private final Vote.VoteAccessor accessor;

    public EntityProvider(Cassandra cassandra, Class<? extends Vote.VoteAccessor> accessorClazz) {
        accessor = cassandra.getMappingManager().createAccessor(accessorClazz);
    }

    public Vote getVoteByMessage(long messageId) {
        return accessor.getVoteByMessage(messageId).one();
    }

    public Vote getVoteByMember(long guildId, long authorId) {
        return accessor.getVoteByMember(guildId, authorId).one();
    }
}
