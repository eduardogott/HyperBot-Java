package me.eduardogottert.commands.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import me.eduardogottert.Main;


public class ShortenCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(ShortenCommand.class);
    private static String[] aliases = {"shorten", "tinyurl", "shorturl", "shortener", "urlshortener", "urlshorten"};
    private static String usage = "Usage: !shorten {url}";

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        String[] splitCommand = event.getMessageContent().split("\\s+");

        if (!Main.parseCommand(aliases, splitCommand[0])) {
            return;
        }

        String content = event.getMessageContent();
        String executor = event.getMessageAuthor().asUser().orElse(null) != null ? event.getMessageAuthor().asUser().orElse(null).getName() + " (" + event.getMessageAuthor().asUser().orElse(null).getId() + ")" : "Unknown User";
        String channel = event.getChannel().asServerTextChannel().isPresent() ? event.getChannel().asServerTextChannel().get().getName() + " (" + event.getChannel().asServerTextChannel().get().getId() + ")" : "Unknown Channel";
        logger.info("Command '" + content + "' executed by " + executor + " at #" + channel);

        if (splitCommand.length < 2) {
            event.getChannel().sendMessage(usage).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        }

        String url = splitCommand[1];
        String regex = "^(http(s)?://)?([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?$";

        if (!url.matches(regex)) {
            event.getChannel().sendMessage("Invalid URL").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        }


        String tinyUrl = "https://tinyurl.com/api-create.php?url=" + url;

        try {
            URL apiURL = new URL(tinyUrl);
            HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String shortenedUrl = in.readLine();
                in.close();

                event.getChannel().sendMessage("Shortened URL: " + shortenedUrl).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            } else {
                event.getChannel().sendMessage("Failed to shorten URL").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            }
        } catch (IOException e) {
            event.getChannel().sendMessage("Failed to connect to the TinyURL API").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
        }
    }
}