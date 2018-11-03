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
import me.schlaubi.votebot.util.SafeMessage;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.Helpers;

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
    @Transient
    private Guild guild;
    @Transient
    private Member member;

    @PartitionKey
    @Column(name = "guild_id")
    private long guildId;
    @PartitionKey(1)
    @Column(name = "author_id")
    private long authorId;
    private String heading;
    private List<String> options;
    /*          userId voteId */
    @Column(name = "user_votes")
    private Map<Long, Integer> userVotes = new HashMap<>();
    /*          unicode voteId */
    private Map<String, Integer> emotes;
    /*          messageId textChannelId */
    private Map<Long, Long> messages;
    @Column(name = "vote_counts")
    /*          userId amount of votes*/
    private Map<Long, Integer> voteCounts = new HashMap<>();


    public Vote() {
        super(Vote.class, VoteBot.getInstance().getDatabaseConnection(), "[VOTE]");
    }

    public Vote(long guildId, long authorId, String heading, List<String> options, Map<String, Integer> emotes, Map<Long, Long> messages) {
        super(Vote.class, VoteBot.getInstance().getDatabaseConnection(), "[VOTE]");
        this.guildId = guildId;
        this.authorId = authorId;
        this.heading = heading;
        this.options = options;
        this.emotes = emotes;
        this.messages = messages;
        save(this);
    }

    public void close() {
        delete(this);
        crawlMessages().forEach(message -> message.delete().queue());
    }

    public void addEmotes(Message voteMessage) {
        emotes.keySet().forEach(emote -> {
            if (Helpers.isNumeric(emote))
                voteMessage.addReaction(voteMessage.getGuild().getEmoteById(emote)).queue();
            else
                voteMessage.addReaction(emote).queue();
        });
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
        AtomicInteger count = new AtomicInteger();
        options.forEach(option -> {
                    long voteCount = getVoteCountById(count.get());
                    String emoteRaw = Misc.getValueByKey(emotes, count.get());
                    answersBuilder.append("**").append(count.get() + 1).append("**").append(". ").append(Helpers.isNumeric(emoteRaw) ? Misc.mentionEmote(emoteRaw, getGuild()) :  emoteRaw).append(" - ").append(option).append(": `").append(voteCount).append("`").append(System.lineSeparator());
                    count.incrementAndGet();
                }
        );
        builder.setDescription(answersBuilder.toString());
        return builder;
    }

    public void vote(Integer voteId, User user) {
        long userId = user.getIdLong();
        int voteCount = 0;
        if (userVotes.containsKey(userId)) {
            userVotes.remove(userId);
            if (voteCounts.containsKey(userId)) {
                voteCount = voteCounts.get(userId);
                voteCounts.remove(userId);
            }
        }
        voteCount++;
        voteCounts.put(userId, voteCount);
        userVotes.put(userId, voteId);
        save();
    }

    public void updateMessages() {
        executor.execute(() -> crawlMessages().forEach(message -> SafeMessage.editMessage(message, buildEmbed(message.getIdLong()))));
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
        return voteCounts.get(user.getIdLong()) <= 2;
    }

    public long getVoteCountById(Integer voteId) {
        return userVotes.entrySet().stream().filter(entry -> entry.getValue().equals(voteId)).count();
    }

}
