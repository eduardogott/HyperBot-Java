package me.eduardogottert.commands.utils.infos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;
import java.awt.Color;
import java.util.Optional;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.user.User;

import me.eduardogottert.Main;

public class GuildIconCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(GuildIconCommand.class);
    private static String[] aliases = {"guildicon", "servericon"};
    private static final Color PURPLE = new Color(128, 0, 128);

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

        Optional<Icon> icon = event.getServer().get().getIcon();
        String iconUrl;

        if (icon.isPresent() && icon.get().getUrl() != null) {
            iconUrl = icon.get().getUrl().toString();
        } else {
            event.getChannel().sendMessage("This server doesn't have an icon").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle(event.getMessage().getChannel().asServerTextChannel().get().getServer().getName() + " icon (Click to download)")
            .setImage(iconUrl)
            .setColor(PURPLE)
            .setUrl(iconUrl);

        User executorUser = event.getMessageAuthor().asUser().orElse(null);
        String executorAvatarUrl = executorUser != null ? executorUser.getAvatar().getUrl().toString() : null;
                
        if (executorAvatarUrl != null) {
            embed.setFooter("Command executed by " + executor, executorAvatarUrl);
        } else {
            embed.setFooter("Command executed by " + executor);
        }
            

        event.getChannel().sendMessage(embed).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
    }
}