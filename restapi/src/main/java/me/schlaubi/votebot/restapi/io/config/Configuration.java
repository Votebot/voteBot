package me.schlaubi.votebot.restapi.io.config;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

@Log4j2
public class Configuration extends YamlFile {

    public Configuration(File file){
        super(file);
        if (!exists()) {
            try {
                createNewFile(true);
                Files.copy(Path.of(ClassLoader.getSystemResource("config.yml").toURI()), new FileOutputStream(file));
            } catch (IOException | URISyntaxException e) {
                log.error("[Configuration] Error while creating config file", e);
            }
        }
        try {
            load();
        } catch (IOException | InvalidConfigurationException e) {
            log.error("[Configuration] Error while loading config file", e);
        }
        log.info("[Configuration] Loaded config");
    }

    private void setDefault(@NonNull YamlFile config) {
        config.addDefault("bot", "tokenheyhey");
    }
}
