package me.eduardogottert.commands.moderation.punishments;

import java.util.List;
import java.awt.Color;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;

import me.eduardogottert.Main;
import me.eduardogottert.discordUtils.Markdown;

public class KickCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(KickCommand.class);
    private static String[] aliases = {"kick"};
    private static String usage = "Usage: !kick {@user} [reason]";

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

        User userToKick;
        final String reason;

        if (splitCommand.length < 2) {
            event.getChannel().sendMessage(usage);
            return;
        } else if (splitCommand.length < 3) {
            reason = "Reason not specified";
        } else {
            reason = content.substring(content.indexOf(" ", content.indexOf(" ") + 1) + 1);
        }

        List<User> mentionedUsers = event.getMessage().getMentionedUsers();
        userToKick = mentionedUsers.size() != 0 ? mentionedUsers.get(0) : null;

        if (userToKick == null) {
            event.getChannel().sendMessage("User not found.");
            return;
        }
        
        if (userToKick == event.getApi().getYourself()) {
            event.getChannel().sendMessage("You can't kick me!");
            return;
        }

        if (userToKick == event.getMessageAuthor().asUser().orElse(null)) {
            event.getChannel().sendMessage("You can't kick yourself!");
            return;
        }


        event.getServer().ifPresent(server -> {
            server.kickUser(userToKick, reason).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
        });
        
        String userName = userToKick.getMentionTag();

        User executorUser = event.getMessageAuthor().asUser().orElse(null);

        String executorName;
        String executorAvatarUrl;

        executorName = executorUser != null ? executorUser.getMentionTag() : "Unknown User";
        executorAvatarUrl = executorUser != null ? executorUser.getAvatar().getUrl().toString() : null;

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("New kick!")
            .addField("Kicked user", userName, true)
            .addField("Kicked by", executorName, true)
            .addField("Reason", Markdown.italic(reason), false)
            .setColor(Color.YELLOW);
                
        if (executorAvatarUrl != null) {
            embed.setFooter("Command executed by " + executor, executorAvatarUrl);
        } else {
            embed.setFooter("Command executed by " + executor);
        }

        event.getChannel().sendMessage(embed).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
        
        event.getMessage().delete("Command executed successfully!").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
    }
}