package me.schlaubi.votebot.core;

import lombok.Getter;
import me.schlaubi.votebot.VoteBot;
import me.schlaubi.votebot.core.cache.VoteCache;
import me.schlaubi.votebot.core.entities.Vote;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteManager {

    @Getter
    private final VoteCache cache;

    public VoteManager(VoteBot bot) {
        this.cache = new VoteCache(bot);
    }

    public Vote createVote(Member author, String heading, List<String> options, Map<String, String> emotes, Message initialMessage) {
        Map<Long, Long> messages = new HashMap<>();
        messages.put(initialMessage.getIdLong(), initialMessage.getTextChannel().getIdLong());
        Map<String, Integer> answers = new HashMap<>();
        options.stream().distinct().forEach(option -> answers.put(option, 0));
        Vote vote = new Vote(author.getGuild().getIdLong(), author.getUser().getIdLong(), heading, answers, emotes, messages);
        cache.add(vote);
        return vote;
    }

    public boolean hasVote(Member member) {
        return cache.hasVote(member.getGuild(), member.getUser());
    }

    public Vote getVote(Member member) {
        return cache.getVote(member.getGuild(), member.getUser());
    }

    public void closeVote(Vote target) {
        target.close();
        cache.remove(target);
    }
}
