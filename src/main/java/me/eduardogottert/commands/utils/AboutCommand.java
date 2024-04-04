package me.eduardogottert.commands.utils;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import java.awt.Color;
import me.eduardogottert.Main;

public class AboutCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(AboutCommand.class);
    
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageContent().equalsIgnoreCase(Main.prefix + "about")) {
            String content = event.getMessageContent();
            String executor = event.getMessageAuthor().asUser().orElse(null) != null ? event.getMessageAuthor().asUser().orElse(null).getName() + " (" + event.getMessageAuthor().asUser().orElse(null).getId() + ")" : "Unknown User";
            String channel = event.getChannel().asServerTextChannel().isPresent() ? event.getChannel().asServerTextChannel().get().getName() + " (" + event.getChannel().asServerTextChannel().get().getId() + ")" : "Unknown Channel";
            logger.info("Command '" + content + "' executed by " + executor + " at #" + channel);
            
            final Color PURPLE = new Color(128, 0, 128);

            EmbedBuilder embed = new EmbedBuilder()
            .setTitle("Informations about HyperBot!")
            .setColor(PURPLE)
            .addField("Created by", "@hyperxzy", false)
            .addField("Created in", "2024", false)
            .addField("Message me", "https://x.com/n0tedu_", false)
            .addField("View me on GitHub", "https://github.com/eduardogott/HyperBotJava", false)
            .setFooter("For more informations type " + Main.prefix + "help");
            
            event.getChannel().sendMessage(embed)
            .exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
        }
    }
}