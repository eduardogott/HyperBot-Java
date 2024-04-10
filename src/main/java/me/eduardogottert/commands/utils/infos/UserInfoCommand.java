//TODO Create a "roles" subcommand

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
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Optional;
import java.util.List;
import java.awt.Color;

import me.eduardogottert.Main;

public class UserInfoCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(UserInfoCommand.class);
    private static String[] aliases = {"userinfo"};
    private static final Color PURPLE = new Color(128, 0, 128);
    DateTimeFormatter formatterDay = DateTimeFormatter.ofPattern("dd").withZone(ZoneId.systemDefault());
    DateTimeFormatter formatterMonth = DateTimeFormatter.ofPattern("MM").withZone(ZoneId.systemDefault());
    DateTimeFormatter formatterYear = DateTimeFormatter.ofPattern("yyyy").withZone(ZoneId.systemDefault());
    DateTimeFormatter formatterHour = DateTimeFormatter.ofPattern("HH").withZone(ZoneId.systemDefault());
    DateTimeFormatter formatterMinute = DateTimeFormatter.ofPattern("mm").withZone(ZoneId.systemDefault());
    DateTimeFormatter formatterSecond = DateTimeFormatter.ofPattern("ss").withZone(ZoneId.systemDefault());

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

        if (splitCommand.length == 2 || !splitCommand[2].equalsIgnoreCase("roles")) {
            String displayName = user.getDisplayName(event.getServer().orElse(null));
            String avatarUrl = user.getAvatar(1024).getUrl().toString();
            String id = user.getIdAsString();
            String name = user.getName();
            String discriminator = user.getDiscriminator();

            Instant creationDate = user.getCreationTimestamp();
            String creationDay = formatterDay.format(creationDate);
            String creationMonth = formatterMonth.format(creationDate);
            String creationYear = formatterYear.format(creationDate);
            String creationHour = formatterHour.format(creationDate);
            String creationMinute = formatterMinute.format(creationDate);
            String creationSecond = formatterSecond.format(creationDate);

            Optional<Instant> joinDate = user.getJoinedAtTimestamp(event.getServer().orElse(null));
            String joinDay = joinDate.isPresent() ? formatterDay.format(joinDate.get()) : null;
            String joinMonth = joinDate.isPresent() ? formatterMonth.format(joinDate.get()) : null;
            String joinYear = joinDate.isPresent() ? formatterYear.format(joinDate.get()) : null;
            String joinHour = joinDate.isPresent() ? formatterHour.format(joinDate.get()) : null;
            String joinMinute = joinDate.isPresent() ? formatterMinute.format(joinDate.get()) : null;
            String joinSecond = joinDate.isPresent() ? formatterSecond.format(joinDate.get()) : null;

            List<Role> userRoles = user.getRoles(event.getServer().orElse(null));
            String highestRole;

            if (userRoles.isEmpty() || userRoles.get(0).getName().equals("@everyone")) {
                highestRole = "User doesn't have any role";
            } else {
                highestRole = userRoles.get(0).getName();
            }

            EmbedBuilder embed = new EmbedBuilder()
                .setTitle(displayName + "'s info")
                .addField("Discord Name", name + ((discriminator != null && discriminator != "0") ? "" : "#" + discriminator), true)
                .addField("Discord ID", id, true)
                .addField("Creation Date", creationDay + "/" + creationMonth + "/" + creationYear + " " + creationHour + ":" + creationMinute + ":" + creationSecond, true)
                .addField("Join Date", ((joinDate.isPresent()) ? joinDay + "/" + joinMonth + "/" + joinYear + " " + joinHour + ":" + joinMinute + ":" + joinSecond : "Hasn't joined this guild"), true)
                .addField("Highest Role", highestRole, true)
                .setImage(avatarUrl)
                .setColor(PURPLE)
                .setFooter("Command executed by " + executor);
            
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
                if (role.getName() == "@everyone") {
                    userRoles.remove(role);
                } else {
                    userRolesString = userRolesString + role.getMentionTag(); // TODO: do this work?
                }
            }

            if (userRolesString.isEmpty()) {
                event.getChannel().sendMessage("User doesn't have any role").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
                return;
            }

            User executorUser = event.getMessageAuthor().asUser().orElse(null);
            executorAvatarUrl = executorUser != null ? executorUser.getAvatar().getUrl.toString() : null;

            EmbedBuilder embed = new EmbedBuilder()
                .setTitle(displayName + "'s roles")
                .setDescription(userRolesString)
                .setImage(avatarUrl)
                .setColor(PURPLE);
            
            if (executorAvatarUrl != null) {
                embed.setFooter("Command executed by" + executor, executorAvatarUrl);
            } else {
                embed.setFooter("Command executed by" + executor)
            }
            
            event.getChannel().sendMessage(embed).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));

        }
    }
}