package me.schlaubi.votebot;

import lombok.extern.log4j.Log4j2;
import me.schlaubi.votebot.io.config.Configuration;
import me.schlaubi.votebot.io.database.Cassandra;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.IOException;

@Log4j2
public class Launcher {

    public static void main(String[] args) throws IOException {
        Configurator.setRootLevel(args.length == 0 ? Level.INFO : Level.toLevel(args[0], Level.INFO));
        Configurator.initialize(ClassLoader.getSystemClassLoader(), new ConfigurationSource(ClassLoader.getSystemResourceAsStream("log4j2.xml")));
        log.info("[Launcher] VoteBot is launching ...");
        Configuration configuration = new Configuration(new File("config/config.yml"));
        Cassandra databaseConnection = null;
        try {
            databaseConnection = new Cassandra(configuration.getStringList("database.contactpoints").toArray(new String[0]), configuration.getString("database.user"), configuration.getString("database.password"), configuration.getString("database.keyspace"));
        } catch (Exception e) {
            log.fatal("[Database] An error occurred while connecting to the database", e);
            System.exit(1);
        }
        new VoteBot(databaseConnection, configuration, String.join(" ", args).contains("debug"));
    }
}
