package me.schlaubi.votebot.core;

import me.schlaubi.votebot.util.NameThreadFactory;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GameAnimator implements Runnable {

    private final ScheduledExecutorService scheduler;
    private final ShardManager shardManager;
    private final Game[] games;
    private final ThreadLocalRandom generator;

    public GameAnimator(ShardManager shardManager, YamlFile config) {
        this.shardManager = shardManager;
        generator = ThreadLocalRandom.current();
        List<Game> gameList = new ArrayList<>();
        config.getStringList("games").forEach(gameRaw -> {
            String[] gameSplitted = gameRaw.split(": ");
            gameList.add(Game.of(Game.GameType.fromKey(Integer.parseInt(gameSplitted[0])), formatGame(gameSplitted[1])));
        });
        this.games = gameList.toArray(new Game[0]);
        this.scheduler = Executors.newSingleThreadScheduledExecutor(new NameThreadFactory("GameAnimator"));
        shardManager.setStatus(OnlineStatus.ONLINE);
        run();
        scheduler.scheduleAtFixedRate(this, 0, 1, TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        shardManager.setGame(games[generator.nextInt(games.length)]);
    }

    private String formatGame(String game) {
        return game.replace("%guilds%", String.valueOf(shardManager.getGuilds().size()));
    }
}
