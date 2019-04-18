/*
 * VoteBot - A unique Discord bot for surveys
 *
 * Copyright (C) 2019  Michael Rittmeister
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/.
 */

package me.schlaubi.votebot.entities;

import cc.hawkbot.regnum.client.util.Colors;
import cc.hawkbot.regnum.client.util.Misc;
import cc.hawkbot.regnum.entities.cassandra.CassandraEntity;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.*;
import me.schlaubi.votebot.core.VoteCache;
import me.schlaubi.votebot.core.VoteController;
import me.schlaubi.votebot.util.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.internal.utils.Helpers;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
@Table(name = "votes")
public class Vote extends CassandraEntity<Vote> {

    @Transient
    public VoteCache cache;

    @PartitionKey
    @Column(name = "guild")
    private long guildId;
    @Column(name = "messages")
    // Message id -> textChannelId
    private Map<Long, Long> messagesIds;
    @PartitionKey(1)
    @Column(name = "author")
    private long authorId;
    private String heading;
    private List<String> options;
    // User id -> answer id
    private Map<Long, List<Integer>> answers;
    // User id -> times of opinion changes
    @Column(name = "emote_mapping")
    private Map<String, Integer> emoteMapping;
    @Column(name = "vote_counts")
    private Map<Long, Integer> voteCounts;
    @Column(name = "maximum_votes")
    private int maximumVotes;
    @Column(name = "maximum_changes")
    private int maximumChanges;
    @SuppressWarnings("CanBeFinal")
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    /**
     * Needed for Cassandra.
     */
    @SuppressWarnings("unused")
    public Vote() {
    }

    public Vote(long guildId, Map<Long, Long> messagesIds, long authorId, String heading, List<String> options, Map<Long, List<Integer>> answers, Map<String, Integer> emoteMapping, Map<Long, Integer> voteCounts, int maximumVotes, int maximumChanges) {
        this.guildId = guildId;
        this.messagesIds = messagesIds;
        this.authorId = authorId;
        this.heading = heading;
        this.options = options;
        this.answers = answers;
        this.emoteMapping = emoteMapping;
        this.voteCounts = voteCounts;
        this.maximumVotes = maximumVotes;
        this.maximumChanges = maximumChanges;
    }

    public EmbedBuilder renderVote() {
        var user = getAuthor().getUser();
        var builder = new EmbedBuilder()
                .setColor(Colors.BLURLPLE)
                .setAuthor(user.getName(), "https://votevot.hawkbot.cc/view/", user.getAvatarUrl())
                .setFooter("React or use the vote command to vote", user.getJDA().getSelfUser().getAvatarUrl())
                .setTimestamp(createdAt)
                .setTitle(heading);
        StringBuilder answers = new StringBuilder();
        AtomicInteger count = new AtomicInteger();
        options.forEach(option -> {
            var voteCount = this.answers.values().stream().filter(chosen -> chosen.contains(count.get())).count();
            var emoteRaw = Misc.getKeyOrNullByValue(emoteMapping, count.get());
            assert emoteRaw != null;
            answers.append("**").append(count.getAndIncrement() + 1).append("**").append(". ").append(Helpers.isNumeric(emoteRaw) ? Utils.mentionEmote(emoteRaw, getGuild()) : emoteRaw).append(" - ").append(option).append(": `").append(voteCount).append('`').append(System.lineSeparator());
        });
        builder.setDescription(answers);
        return builder;
    }

    @Override
    public CompletionStage<Void> saveAsync() {
        cache.updateVote(this);
        return super.saveAsync();
    }

    @Override
    public CompletionStage<Void> deleteAsync() {
        cache.deleteVote(this);
        return super.deleteAsync();
    }

    public long getGuildId() {
        return guildId;
    }

    @Transient
    public Guild getGuild() {
        return cache.getBot().getRegnum().getDiscord().getShardManager().getGuildById(guildId);
    }

    public Map<Long, Long> getMessagesIds() {
        return messagesIds;
    }

    @Transient
    public CompletionStage<Map<Message, TextChannel>> getMessages() {
        return CompletableFuture.supplyAsync(() -> {
            var messages = new HashMap<Message, TextChannel>();
            var guild = getGuild();
            messagesIds.forEach((message, channel) -> {
                var textChannel = guild.getTextChannelById(channel);
                messages.put(textChannel.retrieveMessageById(message).complete(), textChannel);
            });
            return messages;
        }, VoteCache.getTHREAD_POOL());

    }

    public void initialize() {
        if (answers == null) {
            answers = new HashMap<>();
        }
        if (voteCounts == null) {
            voteCounts = new HashMap<>();
        }
    }

    @Transient
    public VoteController getController() {
        return new VoteController(this);
    }

    public long getAuthorId() {
        return authorId;
    }

    @Transient
    public Member getAuthor() {
        return getGuild().getMemberById(authorId);
    }

    public String getHeading() {
        return heading;
    }

    public List<String> getOptions() {
        return options;
    }

    public Map<Long, List<Integer>> getAnswers() {
        return answers;
    }

    public Map<String, Integer> getEmoteMapping() {
        return emoteMapping;
    }

    public Map<Long, Integer> getVoteCounts() {
        return voteCounts;
    }

    public int getMaximumVotes() {
        return maximumVotes;
    }

    public int getMaximumChanges() {
        return maximumChanges;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setMessagesIds(Map<Long, Long> messagesIds) {
        this.messagesIds = messagesIds;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @com.datastax.driver.mapping.annotations.Accessor
    public interface Accessor {

        @Query("SELECT * FROM votes")
        Result<Vote> getAll();
    }
}
