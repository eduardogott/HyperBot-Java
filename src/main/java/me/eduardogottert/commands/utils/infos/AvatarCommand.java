package me.eduardogottert.commands.utils.infos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import java.awt.Color;

import me.eduardogottert.Main;

public class AvatarCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(AvatarCommand.class);
    private static String[] aliases = {"avatar", "pfp", "profilepic", "profilepicture", "icon", "usericon", "useravatar", "userpfp", "userprofilepic", "userprofilepicture"};
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

        User user;

        if (splitCommand.length == 1) {
            String userToGetAvatar = event.getMessageAuthor().getIdAsString();
            user = event.getApi().getUserById(userToGetAvatar).join();
        } else {
            String userToGetAvatar = splitCommand[1];
            
            if (userToGetAvatar.startsWith("<@") && userToGetAvatar.endsWith(">")) {
                String userId = userToGetAvatar.substring(2, userToGetAvatar.length() - 1);
                user = event.getApi().getUserById(userId).join();
            } else if (userToGetAvatar.matches("\\d+")) {
                user = event.getApi().getUserById(userToGetAvatar).join();
            } else {
                user = event.getApi().getCachedUserByDiscriminatedName(userToGetAvatar).orElse(null);
                if (user == null) {
                    event.getChannel().sendMessage("Invalid user").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
                    return;
                }
            }
        }

        String avatarUrl = user.getAvatar().getUrl().toString();

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle(event.getMessageAuthor().asUser().toString() + "'s avatar")
            .setImage(avatarUrl)
            .setColor(PURPLE)
            .setFooter("Command executed by " + executor);

        event.getChannel().sendMessage(embed);
    }
}