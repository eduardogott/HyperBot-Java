package me.eduardogottert.commands.moderation.management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;
import org.javacord.api.entity.message.MessageSet;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import me.eduardogottert.Main;

public class ClearCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(ClearCommand.class);
    private static String[] aliases = {"clear", "purge", "clearchat"};
    private static String usage = "Usage: !clear {amount} [reason]";

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

        TextChannel channelToClear = event.getChannel().asTextChannel().get();
        final int amount;
        final String reason;

        if (splitCommand.length < 2) {
            event.getChannel().sendMessage(usage).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        } else if (splitCommand.length < 3) {
            reason = "Reason not specified";
        } else {
            reason = content.substring(content.indexOf(" ", content.indexOf(" ")) + 1);
        }

        try {
            amount = Integer.parseInt(splitCommand[1]);
        } catch (Exception e) {
            e.printStackTrace();
            event.getChannel().sendMessage("You didn't specify the amount of messages to be deleted. Perhaps it is not an integer.\n" + usage).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        }

        boolean capped = false;
        if (amount > 100) {
            capped = true;
        }

        if (channelToClear == null) {
            event.getChannel().sendMessage("Couldn't find the channel to delete messages! Perhaps you are in a DM?").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        }

        MessageSet messagesToDelete;
        try {
            messagesToDelete = channelToClear.getMessages(amount).get();
        } catch (Exception e) {
            e.printStackTrace();
            event.getChannel().sendMessage("Couldn't delete the messages.").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            logger.error("Couldn't delete messages.");
            return;
        }

        try {
            for (Message messageToDelete : messagesToDelete) {
                messageToDelete.delete("Bulk deleted by " + executor + " for " + reason).exceptionally(ExceptionLogger.get());
            }
        } catch (Exception e) {
            event.getChannel().sendMessage("Couldn't delete all messages. Perhaps some are older than two weeks.").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        }

        Message message;
        if (capped == false) {
            message = event.getChannel().sendMessage("Deleted " + amount + " messages.").exceptionally(ExceptionLogger.get(MissingPermissionsException.class)).join();
            
        } else {
            message = event.getChannel().sendMessage("Deleted 100 messages. Capped due to Discord limitations.").exceptionally(ExceptionLogger.get(MissingPermissionsException.class)).join();
        }

        Main.deleteAfter(message, 5000, "milliseconds");
    }
}