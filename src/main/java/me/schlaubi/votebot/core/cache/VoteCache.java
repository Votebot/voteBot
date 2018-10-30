package me.schlaubi.votebot.core.cache;

import me.schlaubi.votebot.VoteBot;
import me.schlaubi.votebot.core.entities.Vote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.util.List;
import java.util.Optional;

public class VoteCache {

    private final List<Vote> cacheList;

    public VoteCache(VoteBot bot) {
        Vote.VoteProvider provider = bot.getDatabaseConnection().getMappingManager().createAccessor(Vote
                .VoteProvider.class);
        cacheList = provider.getAll().all();
    }

    public boolean hasVote(Guild guild, User user) {
        return cacheList.stream().anyMatch(vote -> vote.getGuildId() == guild.getIdLong() && vote.getAuthorId() == user.getIdLong());
    }

    public boolean isPollMessage(long messageId) {
        return cacheList.stream().anyMatch(vote -> vote.getMessages().containsKey(messageId));
    }

    public Vote getVote(long messageId) {
        final Optional<Vote> optional = cacheList.stream().filter(vote -> vote.getMessages().containsKey(messageId)).findFirst();
        return optional.orElse(null);
    }

    public Vote getVote(Guild guild, User user) {
        final Optional<Vote> optional = cacheList.stream().filter(vote -> vote.getGuildId() == guild.getIdLong() && vote.getAuthorId() == user.getIdLong()).findFirst();
        return optional.orElse(null);
    }

    public void remove(Vote vote) {
        cacheList.remove(vote);
    }

    public void add(Vote vote) {
        cacheList.add(vote);
    }

    public void update(Vote newVote) {
        cacheList.removeIf(vote -> vote.getGuildId() == newVote.getGuildId() && vote.getAuthorId() == newVote.getAuthorId());
        newVote.save(newVote);
        cacheList.add(newVote);
    }
}
