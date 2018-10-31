package me.schlaubi.votebot.core.entities;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.*;
import lombok.Getter;
import lombok.ToString;
import me.schlaubi.votebot.VoteBot;
import me.schlaubi.votebot.io.database.DatabaseEntity;
import me.schlaubi.votebot.util.Colors;
import me.schlaubi.votebot.util.Misc;
import me.schlaubi.votebot.util.NameThreadFactory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.User;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Table(name = "votes")
@ToString
public class Vote extends DatabaseEntity<Vote> {

    @Transient
    private static ExecutorService executor = Executors.newCachedThreadPool(new NameThreadFactory("VoteMessageUpdater"));

    @PartitionKey
    @Column(name = "guild_id")
    private long guildId;
    @PartitionKey(1)
    @Column(name = "author_id")
    private long authorId;
    private String heading;
    private Map<String, Integer> answers;
    @Column(name = "user_votes")
    private Map<Long, String> userVotes = new HashMap<>();
    private Map<String, String> emotes;
    private Map<Long, Long> messages;
    @Column(name = "vote_counts")
    private Map<Long, Integer> voteCounts = new HashMap<>();
    @Transient
    private Guild guild = null;
    @Transient
    private Member member;

    public Vote() {
        super(Vote.class, VoteBot.getInstance().getDatabaseConnection(), "[VOTE]");
    }

    public Vote(long guildId, long authorId, String heading, Map<String, Integer> answers, Map<String, String> emotes, Map<Long, Long> messages) {
        super(Vote.class, VoteBot.getInstance().getDatabaseConnection(), "[VOTE]");
        this.guildId = guildId;
        this.authorId = authorId;
        this.heading = heading;
        this.answers = answers;
        this.emotes = emotes;
        this.messages = messages;
        save(this);
    }

    public void close() {
        delete(this);
        crawlMessages().forEach(message -> message.delete().queue());
    }

    public void addEmotes(Message voteMessage) {
        emotes.keySet().forEach(emote -> voteMessage.addReaction(emote).queue());
    }

    public void addMessage(Message message) {
        messages.put(message.getIdLong(), message.getTextChannel().getIdLong());
        save();
    }

    @Transient
    public void changeHeading(String heading) {
        this.heading = heading;
        save();
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

    public void save() {
        VoteBot.getInstance().getVoteManager().getCache().update(this);
    }

    public EmbedBuilder buildEmbed(long messageId) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Colors.BLURPLE)
                .setAuthor(getMember().getUser().getName(), "https://votebot.schlaubi.me/vote/view/" + messageId, getMember().getUser().getAvatarUrl())
                .setFooter("React to vote", null)
                .setTimestamp(Instant.now())
                .setTitle(heading);
        StringBuilder answersBuilder = new StringBuilder();
        AtomicInteger count = new AtomicInteger(1);
        answers.forEach((option, votes) -> answersBuilder.append("**").append(count.getAndAdd(1)).append("**").append(". ").append(Misc.getValueByKey(emotes, option)).append(" - ").append(option).append(": `").append(votes).append("`").append(System.lineSeparator()));
        builder.setDescription(answersBuilder.toString());
        return builder;
    }

    public void vote(String option, User user) {
        if (!voteCounts.containsKey(user.getIdLong())) {
            voteUp(option, user, true);
            voteCounts.put(user.getIdLong(), 1);
        } else {
            //Get oldOption
            var oldOption = userVotes.get(user.getIdLong());
            var oldOptionVoteCount = answers.get(oldOption);
            oldOptionVoteCount = oldOptionVoteCount - 1;
            //Decrease old option
            answers.replace(oldOption, oldOptionVoteCount);
            userVotes.replace(user.getIdLong(), option);
            //Increase new option
            voteUp(option, user, false);
            voteCounts.put(user.getIdLong(), (voteCounts.get(user.getIdLong()) + 1));
        }
        save();
    }

    private void voteUp(String option, User user, boolean first) {
        var currentVotes = answers.get(option);
        currentVotes = currentVotes + 1;
        answers.replace(option, currentVotes);
        if (first)
            userVotes.put(user.getIdLong(), option);
    }


    public void updateMessages() {
        executor.execute(() -> crawlMessages().forEach(message -> message.editMessage(buildEmbed(message.getIdLong()).build()).queue()));
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

    public List<Message> crawlMessages() {
        List<Message> out = new ArrayList<>();
        messages.forEach((messageId, channelId) -> {
            TextChannel channel = getGuild().getTextChannelById(channelId);
            if (channel == null) {
                messages.remove(messageId);
                return;
            }
            out.add(channel.getMessageById(messageId).complete());
        });
        return out;
    }


    public boolean isPermitted(User user) {
        if (!voteCounts.containsKey(user.getIdLong()))
            return true;
        return voteCounts.get(user.getIdLong()) <= 3;
    }
}
