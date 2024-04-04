package me.eduardogottert.commands.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.entity.message.Message;

import me.eduardogottert.Main;

public class PollCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(PollCommand.class);
    private static String[] aliases = {"poll", "vote", "survey", "question", "ask"};
    private static String[] unicodes = {"\u0031", "\u0032", "\u0033", "\u0034", "\u0035", "\u0036", "\u0037", "\u0038", "\u0039"};
    private static String usage = "Usage: !poll {options, separed by comma} [-e/--everyone] [-h/--here]";
    
    public static boolean[] parsePlaceholders(String[] options) {
        boolean[] result = {false, false};

        for (int i = 0; i < options.length; i++) {
            if (options[i].equalsIgnoreCase("-e") || options[i].equalsIgnoreCase("--everyone")) {
                result[0] = true;
            }
            if (options[i].equalsIgnoreCase("-h") || options[i].equalsIgnoreCase("--here")) {
                result[1] = true;
            }
        }
        return result;
    }

    public static String[] joinOptions(String[] options) {
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < options.length; i++) {
            if (!options[i].equalsIgnoreCase("-e") && !options[i].equalsIgnoreCase("--everyone") && !options[i].equalsIgnoreCase("-h") && !options[i].equalsIgnoreCase("--here")) {
                result.append(options[i]);
                if (i < options.length - 1) {
                    result.append(" ");
                }
            }
        }
        return result.toString().split(",");
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

        boolean[] placeholders = parsePlaceholders(splitCommand);
        String[] options = joinOptions(splitCommand);

        StringBuilder pollMessage = new StringBuilder();
        pollMessage.append("Poll: ");

        if (options.length < 1 || options[0].isEmpty()) {
            event.getChannel().sendMessage(usage);
            return;
        }

        if (options.length > 9) {
            event.getChannel().sendMessage("You can only have up to 9 options in a poll.");
            return;
        }

        if (options.length > 1) {
            pollMessage.append("Choose one of the following options:");
        } else {
            pollMessage.append("Do you agree with the following statement?");
        }

        pollMessage.append("\nPlease vote using the emojis below.\n");

        if (options.length > 1) {
            for (int i = 0; i < options.length; i++) {
                pollMessage.append("\n").append(unicodes[i] + "\u20E3").append(" ").append(" " + options[i].trim());
            }
        } else {
            pollMessage.append("\n❓").append(" ").append(options[0].trim());
        }

        if (placeholders[0]) {
            pollMessage.append("\n\n||@everyone||");
        } else if (placeholders[1]) {
            pollMessage.append("\n\n||@here||");
        }

        Message message = event.getChannel().sendMessage(pollMessage.toString()).join();

        if (message != null) {
            if (options.length > 1) {
                for (int i = 0; i < options.length; i++) {
                    message.addReaction(unicodes[i] + "\uFE0F" + "\u20E3");
                }
            } else {
                message.addReaction("✅");
                message.addReaction("❌");
            }
        }

    }
}