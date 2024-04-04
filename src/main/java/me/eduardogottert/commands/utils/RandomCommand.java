package me.eduardogottert.commands.utils;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;

import me.eduardogottert.Main;

public class RandomCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(RandomCommand.class);
    private static String usage = "Usage: !random {min} {max}";
    private static String[] aliases = {"random"};
    
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
        long[] numbers;

        logger.info("Command '" + content + "' executed by " + executor + " at #" + channel); 

        if (splitCommand.length != 3) {
            result = getRandomNumber(1, 100);
            event.getChannel().sendMessage("Random number between 1 and 100... " + result).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;

        } else {
            try {
                numbers = new long[]{Long.parseLong(splitCommand[1]), Long.parseLong(splitCommand[2])};
            } catch (NumberFormatException e) {
                event.getChannel().sendMessage(usage).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
                return;
            }

            if (numbers[0] >= 0 && numbers[0] <= Integer.MAX_VALUE &&
            numbers[1] >= 0 && numbers[1] <= Integer.MAX_VALUE) {

                long minNumber;
                long maxNumber;

                if (numbers[1] > numbers[0]) {
                    minNumber = numbers[0];
                    maxNumber = numbers[1];
                } else {
                    minNumber = numbers[1];
                    maxNumber = numbers[0];          
                }

                result = getRandomNumber(minNumber, maxNumber);
                event.getChannel().sendMessage("Random number between " + minNumber + " and " + maxNumber + "... " + result).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
                return;

            } else {
                event.getChannel().sendMessage("The numbers are out of range! (Range: 0-" + Integer.MAX_VALUE + ")").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
                return;
            }
        }       
    }
}