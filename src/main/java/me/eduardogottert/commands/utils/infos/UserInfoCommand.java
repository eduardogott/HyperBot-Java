package me.eduardogottert.commands.utils.infos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import java.time.Instant;
import java.util.Optional;
import java.util.List;
import java.awt.Color;

import me.eduardogottert.discordUtils.DiscordTimestamp.Format;
import me.eduardogottert.discordUtils.DiscordTimestamp;
import me.eduardogottert.discordUtils.Markdown;
import me.eduardogottert.Main;

public class UserInfoCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(UserInfoCommand.class);
    private static String[] aliases = {"userinfo"};
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

        if ((splitCommand.length == 1) || 
            (splitCommand.length >= 2 && splitCommand[1].equalsIgnoreCase("roles"))) {
            String userToGetAvatar = event.getMessageAuthor().getIdAsString();
            user = event.getApi().getUserById(userToGetAvatar).join();
        } else {
            String userToGetAvatar = splitCommand[1];
            
            if (userToGetAvatar.startsWith("<@") && userToGetAvatar.endsWith(">")) {
                String userId = userToGetAvatar.substring(2, userToGetAvatar.length() - 1);
                user = event.getApi().getUserById(userId).join();
            } else if (userToGetAvatar.matches("\\d+")) {
                user = event.getApi().getUserById(userToGetAvatar).join();
            } else if (userToGetAvatar.matches("^(.{2,32})#([0-9]{4})$")){
                user = event.getApi().getCachedUserByDiscriminatedName(userToGetAvatar).orElse(null);
            } else {
                user = event.getApi().getCachedUserByNameAndDiscriminator(userToGetAvatar, "0").orElse(null);
            }

            if (user == null) {
                event.getChannel().sendMessage("Invalid user").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
                return;
            }
        }

        if ((splitCommand.length == 1) ||
            (splitCommand.length == 2 && !splitCommand[1].equalsIgnoreCase("roles")) ||
            (splitCommand.length >= 3 && !splitCommand[2].equalsIgnoreCase("roles"))) {
            String displayName = user.getDisplayName(event.getServer().orElse(null));
            String avatarUrl = user.getAvatar(1024).getUrl().toString();
            String id = user.getIdAsString();
            String name = user.getName();
            String discriminator = user.getDiscriminator();

            long creationDateEpoch = user.getCreationTimestamp().getEpochSecond();
            String creationDate = DiscordTimestamp.getTimestamp(Format.SHORT_DATE_TIME, creationDateEpoch);

            Optional<Instant> joinDateTimestamp = user.getJoinedAtTimestamp(event.getServer().orElse(null));
            long joinDateEpoch = joinDateTimestamp.isPresent() ? joinDateTimestamp.get().getEpochSecond() : 0;
            String joinDate = joinDateEpoch != 0 ? DiscordTimestamp.getTimestamp(Format.SHORT_DATE_TIME, joinDateEpoch) : "Hasn't joined this guild";

            List<Role> userRoles = user.getRoles(event.getServer().orElse(null));
            String highestRole;

            if (userRoles.isEmpty() || (userRoles.get(0).getName().equals("@everyone") && userRoles.size() == 1)) {
                highestRole = "User doesn't have any role";
            } else {
                highestRole = userRoles.get(userRoles.size()-1).getMentionTag();
            }

            EmbedBuilder embed = new EmbedBuilder()
                .setTitle(":crown: " + displayName + "'s info")
                .addField(":label: Discord Name", Markdown.code(name + ((discriminator != null && discriminator != "0") ? "" : "#" + discriminator)), true)
                .addField(":id: Discord ID", Markdown.code(id), true)
                .addField(":calendar: Creation Date", creationDate, true)
                .addField(":stopwatch: Join Date", joinDate, true)
                .addField(":beginner: Highest Role", highestRole, true)
                .setThumbnail(avatarUrl)
                .setColor(PURPLE);

            User executorUser = event.getMessageAuthor().asUser().orElse(null);
            String executorAvatarUrl = executorUser != null ? executorUser.getAvatar().getUrl().toString() : null;
            String executorName = executorUser != null ? executorUser.getName() : "Unknown User";

            if (executorAvatarUrl != null) {
                embed.setFooter("Command executed by " + executorName, executorAvatarUrl);
            } else {
                embed.setFooter("Command executed by " + executorName);
            }

            event.getChannel().sendMessage(embed).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            
        } else {
            List<Role> userRoles = user.getRoles(event.getServer().orElse(null));
            if (userRoles == null) {
                event.getChannel().sendMessage("Couldn't find the provided user").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
                return;
            }

            String displayName = user.getDisplayName(event.getServer().orElse(null));
            String avatarUrl = user.getAvatar(1024).getUrl().toString();
            String userRolesString = "";

            for (Role role : userRoles) {
                if (!role.getName().equals("@everyone")) {
                    userRolesString = userRolesString + " " + role.getMentionTag();
                }
            }

            if (userRolesString.isEmpty()) {
                userRolesString = "User doesn't have any role";
            }

            EmbedBuilder embed = new EmbedBuilder()
                .setTitle(":beginner: " + displayName + "'s roles")
                .setDescription(userRolesString)
                .setThumbnail(avatarUrl)
                .setColor(PURPLE);
            
            User executorUser = event.getMessageAuthor().asUser().orElse(null);
            String executorAvatarUrl = executorUser != null ? executorUser.getAvatar().getUrl().toString() : null;
            String executorName = executorUser != null ? executorUser.getName() : "Unknown User";

            if (executorAvatarUrl != null) {
                embed.setFooter("Command executed by " + executorName, executorAvatarUrl);
            } else {
                embed.setFooter("Command executed by " + executorName);
            }
            
            event.getChannel().sendMessage(embed).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));

        }
    }
}