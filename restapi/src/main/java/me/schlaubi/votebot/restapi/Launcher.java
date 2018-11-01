package me.schlaubi.votebot.restapi;

import lombok.Getter;
import me.schlaubi.votebot.restapi.entities.EntityProvider;
import me.schlaubi.votebot.restapi.entities.Vote;
import me.schlaubi.votebot.restapi.io.config.Configuration;
import me.schlaubi.votebot.restapi.io.database.Cassandra;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.File;

@SpringBootApplication
public class Launcher {

    private final Configuration configuration;
    private final Cassandra cassandra;
    @Getter
    private static EntityProvider provider;

    public static void main(String[] args) {
        new Launcher();
        SpringApplication.run(Launcher.class, args);
    }

    public Launcher() {
        this.configuration = new Configuration(new File("config/", "restapi.yaml"));
        this.cassandra = new Cassandra(configuration.getStringList("database.contactpoints").toArray(new String[0]), configuration.getString("database.user"), configuration.getString("database.password"), configuration.getString("database.keyspace"));
        provider = new EntityProvider(cassandra, Vote.VoteAccessor.class);
    }
}
