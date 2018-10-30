package me.schlaubi.votebot.core.entities;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.*;
import lombok.Getter;
import lombok.ToString;
import me.schlaubi.votebot.VoteBot;
import me.schlaubi.votebot.io.database.DatabaseEntity;
import me.schlaubi.votebot.util.Colors;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Guild;


import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Table(name = "votes")
@ToString
public class Vote extends DatabaseEntity<Vote> {

    @PartitionKey
    @Column(name = "guild_id")
    private long guildId;
    @PartitionKey(1)
    @Column(name = "author_id")
    private long authorId;
    private String heading;
    private Map<String, Integer> answers;
    private Map<String, String> emotes;
    private List<Long> messages;
    @Transient
    private Guild guild = null;
    @Transient
    private Member member;

    public Vote() {
        super(Vote.class, VoteBot.getInstance().getDatabaseConnection(), "[VOTE]");
    }

    public Vote(long guildId, long authorId, String heading, Map<String, Integer> answers, Map<String, String> emotes, List<Long> messages) {
        super(Vote.class, VoteBot.getInstance().getDatabaseConnection(), "[VOTE]");
        this.guildId = guildId;
        this.authorId = authorId;
        this.heading = heading;
        this.answers = answers;
        this.emotes = emotes;
        this.messages = messages;
        save(this);
    }

    @Accessor
    public interface VoteProvider {

        @Query("SELECT * FROM votes")
        Result<Vote> getAll();

    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vote))
            return false;
        Vote vote = ((Vote) obj);
        return vote.getAuthorId() == authorId && vote.getGuildId() == guildId;
    }

    private void save() {
        VoteBot.getInstance().getVoteManager().getCache().update(this);
    }

    public EmbedBuilder buildEmbed() {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Colors.BLURPLE)
                .setAuthor(getMember().getUser().getName(), getMember().getUser().getAvatarUrl())
                .setFooter("React or run v!vote to vote", getGuild().getSelfMember().getUser().getAvatarUrl())
                .setTimestamp(Instant.now())
                .setTitle(heading)
                ;
        StringBuilder answersBuilder = new StringBuilder();
        String[] emotesArray = emotes.values().toArray(new String[0]);
        AtomicInteger count = new AtomicInteger(1);
        answers.forEach((option, votes) -> answersBuilder.append(emotesArray[count.getAndAdd(1) - 1]).append(" - ").append(option).append(" Votes: `").append(votes).append("`").append(System.lineSeparator()));
        builder.setDescription(answersBuilder.toString());
        return builder;
    }

    public Guild getGuild() {
        if (guild == null)
            this.guild = VoteBot.getInstance().getShardManager().getGuildById(guildId);
        return guild;
    }

    public Member getMember() {
        if (member == null)
            member = getGuild().getMemberById(authorId);
        return member;
    }
}
