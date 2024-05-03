package me.eduardogottert.commands.moderation.punishments;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import java.awt.Color;
import java.time.Duration;
import java.util.List;

import me.eduardogottert.Main;
import me.eduardogottert.discordUtils.Markdown;

public class BanCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(BanCommand.class);
    private static String[] aliases = {"ban", "hackban"};
    private static String usage = "Usage: !ban {@user} [reason]";

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

        User userToBan;
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
        userToBan = mentionedUsers.size() != 0 ? mentionedUsers.get(0) : null;
        if (userToBan == null) {
            event.getChannel().sendMessage("User not found.");
            return;
        }
        
        if (userToBan == event.getApi().getYourself()) {
            event.getChannel().sendMessage("You can't ban me!");
            return;
        }

        if (userToBan == event.getMessageAuthor().asUser().orElse(null)) {
            event.getChannel().sendMessage("You can't ban yourself!");
            return;
        }


        event.getServer().ifPresent(server -> {
            server.banUser(userToBan, Duration.ofDays(7), reason).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
        });
        
        String userName = userToBan.getMentionTag();

        User executorUser = event.getMessageAuthor().asUser().orElse(null);

        String executorName;
        String executorAvatarUrl;

        executorName = executorUser != null ? executorUser.getMentionTag() : "Unknown User";
        executorAvatarUrl = executorUser != null ? executorUser.getAvatar().getUrl().toString() : null;

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("New ban!")
            .addField("Banned user", userName, true)
            .addField("Banned by", executorName, true)
            .addField("Duration", Markdown.code("0 (Permanent)"), true)
            .addField("Reason", Markdown.italic(reason), false)
            .setColor(Color.RED);
                
        if (executorAvatarUrl != null) {
            embed.setFooter("Command executed by " + executor, executorAvatarUrl);
        } else {
            embed.setFooter("Command executed by " + executor);
        }

        event.getChannel().sendMessage(embed).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
        
        event.getMessage().delete("Command executed successfully!").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
    }
}