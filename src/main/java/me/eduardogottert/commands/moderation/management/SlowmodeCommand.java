package me.eduardogottert.commands.moderation.management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;

import me.eduardogottert.Main;

public class SlowmodeCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(SlowmodeCommand.class);
    private static String[] aliases = {""};
    private static String usage = "Usage: !";

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

        int slowmodeSeconds = (int) Main.parseTime(splitCommand[1], "seconds");

        if (slowmodeSeconds < 0) {
            event.getChannel().sendMessage("Invalid time.").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        }
        
        boolean capped = false;
        if (slowmodeSeconds > 21600) {
            slowmodeSeconds = 21600;
            capped = true;
            return;
        }

        event.getChannel().asServerTextChannel().get().updateSlowmodeDelayInSeconds(slowmodeSeconds);

        if (capped) {
            event.getChannel().sendMessage("Slowmode set to 6 hours. Capped due to Discord limitations.").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
        } else {
            event.getChannel().sendMessage("Slowmode set to " + slowmodeSeconds + " seconds.").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
        }

    }
}