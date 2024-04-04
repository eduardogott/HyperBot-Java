package me.eduardogottert.commands.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;

import me.eduardogottert.Main;

public class PingCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(PingCommand.class);

    @Override
    public void onMessageCreate(MessageCreateEvent event) { 
        if (event.getMessageContent().equalsIgnoreCase(Main.prefix + "ping")) {
            String content = event.getMessageContent();
            String executor = event.getMessageAuthor().asUser().orElse(null) != null ? event.getMessageAuthor().asUser().orElse(null).getName() + " (" + event.getMessageAuthor().asUser().orElse(null).getId() + ")" : "Unknown User";
            String channel = event.getChannel().asServerTextChannel().isPresent() ? event.getChannel().asServerTextChannel().get().getName() + " (" + event.getChannel().asServerTextChannel().get().getId() + ")" : "Unknown Channel";
            logger.info("Command '" + content + "' executed by " + executor + " at #" + channel);

            long startTime = System.currentTimeMillis();

            event.getChannel().sendMessage("Pinging...").exceptionally(ExceptionLogger.get(MissingPermissionsException.class))
            .thenAcceptAsync(message -> {
                long endTime = System.currentTimeMillis();
                long ping = endTime - startTime;
                message.edit("Pong! " + ping + "ms")
                .exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            }).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));;
        }
    }
}