package me.eduardogottert.commands.utils.rngs;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;

import me.eduardogottert.Main;

public class DiceCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(DiceCommand.class);
    private static String[] aliases = {"dice", "roll"};
    private static String usage = "Usage: !dice {size}";
    
    private static long getRandomNumber(long min, long max) {
        Random random = new Random();
        return random.nextLong(max+1 - min) + min;    
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
        long result;
        long number;

        logger.info("Command '" + content + "' executed by " + executor + " at #" + channel); 

        if (splitCommand.length < 2) {
            result = getRandomNumber(1, 6);
            event.getChannel().sendMessage("Rolling a D6... " + result).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;

        } else {
            try {
                number = Long.parseLong(splitCommand[1]);
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage(usage).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
                return;
            }

            if (number >= 2 && number <= 10000) {
                result = getRandomNumber(1, number);
                event.getChannel().sendMessage("Rolling a D" + number + "..." + result).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
                return;
                
            } else {
            event.getChannel().sendMessage("The number is out of range! (Range: 2-10000)").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
            }
        }       
    }
}