package me.eduardogottert.commands.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;
import me.eduardogottert.Main;

public class SayCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(SayCommand.class);
    private static String[] aliases = {"say", "echo"};
    private static String usage = "Usage: !say {message} - Placeholders: %everyone%, %here%, %author%";

    private static String parseMessage(String message, String author) {
        return message.replace("%everyone%", "@everyone")
                      .replace("%here%", "@here")
                      .replace("%author%", author)
                      .replace("\\%", "%")
                      .replace("%h", "@here")
                      .replace("%e", "@everyone")
                      .replace("%a", author);
    }

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

        String message = event.getMessageContent().replace(splitCommand[0], "").trim();
        
        if (message.isEmpty()) {
            event.getChannel().sendMessage(usage);
            return;
        }

        String author = event.getMessageAuthor().asUser().orElse(null) != null ? event.getMessageAuthor().asUser().orElse(null).getMentionTag() : "Unknown User";
        event.getChannel().sendMessage(parseMessage(message, author))
            .exceptionally(ExceptionLogger.get(MissingPermissionsException.class));

        event.getMessage().delete();

        logger.info(executor + " sent a message using !say: " + message + " at #" + channel);
    }
}