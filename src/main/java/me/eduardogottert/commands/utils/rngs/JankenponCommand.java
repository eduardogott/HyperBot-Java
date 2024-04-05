package me.eduardogottert.commands.utils.rngs;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;

import me.eduardogottert.Main;

public class JankenponCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(JankenponCommand.class);
    private static String[] aliases = {"rps", "rockpaperscissors", "jankenpon"};
    private static String[] options = {"rock", "paper", "scissors", "r", "p", "s"};
    private static String usage = "Usage: !rps {rock/paper/scissors}";

    private static String getBotChoice() {
            Random random = new Random();
            int choiceIndex = random.nextInt(3);
            return options[choiceIndex];
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

        if (!Main.parseCommand(options, splitCommand[1], false)) {
            event.getChannel().sendMessage(usage).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;            
        }

        String botChoice = getBotChoice();
        String userChoice = splitCommand[1];

        switch (userChoice.toLowerCase()) {
            case "r":
                userChoice = "rock";
                break;
            case "p":
                userChoice = "paper";
                break;
            case "s":
                userChoice = "scissors";
                break;
        }
        
        if (userChoice.equalsIgnoreCase(botChoice)) {
            event.getChannel().sendMessage("I choose " + botChoice + "! It's a draw!").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        } else if ((userChoice.equalsIgnoreCase("rock") && botChoice == "scissors") ||
                   (userChoice.equalsIgnoreCase("paper") && botChoice == "rock") ||
                   (userChoice.equalsIgnoreCase("scissors") && botChoice == "paper")) {
            event.getChannel().sendMessage("I choose " + botChoice + "! You won!").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        } else {
            event.getChannel().sendMessage("I choose " + botChoice + "! I won!").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        }
    }
}