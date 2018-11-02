package me.schlaubi.votebot.restapi.entities;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.*;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Table(name = "votes")
@SuppressWarnings("unused")
public class Vote {

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

    @Accessor
    public interface VoteAccessor {

        @Query("SELECT * FROM votes WHERE messages CONTAINS KEY :messageid")
        Result<Vote> getVoteByMessage(@Param("messageid") Long messageId);
        @Query("SELECT * FROM votes WHERE guild_id = :guildId AND author_id = :userId")
        Result<Vote> getVoteByMember(@Param("guildId") Long guildId, @Param("userId") Long userId);
    }
}
