package me.schlaubi.votebot.core.statistics;

import lombok.extern.log4j.Log4j2;
import me.schlaubi.votebot.io.config.Configuration;
import me.schlaubi.votebot.util.NameThreadFactory;
import net.dv8tion.jda.bot.sharding.ShardManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ServerCountStatistics {

    private final ShardManager shardManager;
    private final long botId;
    private final OkHttpClient requester;
    private final Configuration configuration;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new NameThreadFactory("ServerCountPoster"));

    public ServerCountStatistics(ShardManager shardManager, Configuration configuration) {
        this.shardManager = shardManager;
        this.botId = shardManager.getApplicationInfo().complete().getIdLong();
        this.configuration = configuration;
        this.requester = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .build();
    }

    public synchronized void start() {
        scheduler.scheduleAtFixedRate(this::post, 0, 5, TimeUnit.MINUTES);
    }

    private void post() {
        log.debug("[StatsPoster] Posting servercount");
        var object = new JSONObject()
                .put("server_count", shardManager.getGuilds().size())
                .put("bot_id", botId)
                .put("shards_count", shardManager.getShardsTotal())
                .put("shards", getGuildCounts())
                .put("botlist.space", configuration.getString("discordlists.botlist.space"))
                .put("bots.ondiscord.xyz", configuration.getString("discordlists.bots.ondiscord.xyz"))
                .put("discordboats.xyz", configuration.getString("discordlists.discordboats.xyz"))
                .put("discordboats.club", configuration.getString("discordlists.discordboats.club"))
                .put("discordbotlist.com", configuration.getString("discordlists.discordbotlist.com"))
                .put("discordbot.world", configuration.getString("discordlists.discordbot.world"))
                .put("bots.discord.pw", configuration.getString("discordlists.bots.discord.pw"))
                .put("discordbotlist.xyz", configuration.getString("discordlists.discordbotlist.xyz"))
                .put("discordbots.group", configuration.getString("discordlists.discordbots.group"))
                .put("bots.discordlist.app", configuration.getString("discordlists.bots.discordlist.app"))
                .put("discord.services", configuration.getString("discordlists.discord.services"))
                .put("discordsbestbots.xyz", configuration.getString("discordlists.discordsbestbots.xyz"))
                .put("divinediscordbots.com", configuration.getString("discordlists.divinediscordbots.com"))
                .put("discordbots.org", configuration.getString("discordlists.discordbots.org"))
                .put("discordbotindex.com", configuration.getString("discordlists.discordbotindex.com"))
                ;
        var body = RequestBody.create(null, object.toString());
        var request = new Request.Builder()
                .url("https://botblock.org/api/count")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("User-Agent", String.valueOf(botId))
                .build();
        try (var response = requester.newCall(request).execute()){
            assert response.body() != null;
            if (response.code() != 200)
                log.warn(String.format("[ServerCount] Error while posting stats! Response: %s", response.body().string()));
        } catch (IOException e) {
            log.error("[ServerCount] Error while posting stats!", e);
        }
    }

    private Integer[] getGuildCounts() {
        List<Integer> shardGuildCounts = new ArrayList<>();
        shardManager.getShards().forEach(shard -> shardGuildCounts.add(shard.getGuilds().size()));
        return shardGuildCounts.toArray(new Integer[0]);
    }
}
