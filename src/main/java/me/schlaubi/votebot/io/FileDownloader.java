package me.schlaubi.votebot.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class FileDownloader {

    private String[] URLS = {"https://raw.githubusercontent.com/DRSchlaubi/voteBot/develop/fonts/Product Sans Regular.ttf", "https://raw.githubusercontent.com/DRSchlaubi/voteBot/develop/logo/logo.png"};

    public void downloadFiles() throws IOException, URISyntaxException {
        for (String url : URLS) {
            var splittedURL = url.split("/");
            var fileName = splittedURL[splittedURL.length - 1];
            var folderName = splittedURL[splittedURL.length - 2];
            var outputFile = new File(folderName, fileName);
            if (!outputFile.getParentFile().exists())
                outputFile.getParentFile().mkdirs();
            if (!outputFile.exists()) {
                outputFile.createNewFile();
                URL website = new URL(url.replace(" ", "%20"));
                ReadableByteChannel readChannel = Channels.newChannel(website.openStream());
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                outputStream.getChannel().transferFrom(readChannel, 0, Long.MAX_VALUE);
            }
        }
    }
}
