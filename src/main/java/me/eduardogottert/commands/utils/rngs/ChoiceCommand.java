package me.eduardogottert.commands.utils.rngs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;

import me.eduardogottert.Main;

public class ChoiceCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(ChoiceCommand.class);
    private static String[] aliases = {"choice", "choose", "pick"};

    public static String joinChoices(String[] choices) {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < choices.length; i++) {
            result.append(choices[i].trim());
            if (i < choices.length - 1) {
                result.append(" ");
            }
        }
        return result.toString();
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

        String[] choices = joinChoices(splitCommand).split(",");

        if (choices.length < 2) {
            event.getChannel().sendMessage("You need to provide at least two choices to pick from.");
            return;
        }

        int randomIndex = (int) (Math.random() * choices.length);

        event.getChannel().sendMessage("I choose... " + choices[randomIndex] + "!").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));        
    }
}