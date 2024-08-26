package me.eduardogottert.commands.utils.infos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;
import org.javacord.api.entity.user.UserStatus;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.permission.Role;

import java.util.Optional;
import java.time.Instant;
import java.awt.Color;
import java.util.List;

import me.eduardogottert.discordUtils.DiscordTimestamp.Format;
import me.eduardogottert.discordUtils.DiscordTimestamp;
import me.eduardogottert.discordUtils.Markdown;
import me.eduardogottert.Main;

public class GuildInfoCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(GuildInfoCommand.class);
    private static String[] aliases = {"guildinfo", "serverinfo", "guild"};
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
   
        Server server = event.getServer().isPresent() ? event.getServer().get() : null;

        if (server == null) {
            event.getChannel().sendMessage("This command can only be executed in a guold").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        }
        if (splitCommand.length == 1 || !splitCommand[1].equalsIgnoreCase("roles")) {
            Icon guildIcon = server.getIcon().orElse(null);
            String guildIconString = guildIcon != null ? guildIcon.getUrl().toString() : null;
            String guildName = server.getName();
            String guildId = server.getIdAsString();
            String guildOwner = server.getOwner().get().getName() + " (" + server.getOwner().get().getIdAsString() + ")";
            String guildRegion = server.getRegion().getName();
            String guildBoostCount = String.valueOf(server.getBoostCount());
            long guildChannels = server.getChannels().size();
            long guildVoiceChannels = server.getVoiceChannels().size();
            long guildTextChannels = server.getTextChannels().size();
            long guildMembers = server.getMemberCount();
            long guildOnlineMembers = server.getMembers().stream().filter(member -> member.getStatus().equals(UserStatus.ONLINE)).count();
            long guildOfflineMembers = guildMembers - guildOnlineMembers;
            long guildRoles = server.getRoles().size();
            String highestRole = server.getRoles().stream().filter(role -> role.getPosition() == server.getRoles().size() - 1).findFirst().get().getMentionTag();
            String guildBoostLevel = server.getBoostLevel().toString();
            String guildBoostLevelString;

            switch (guildBoostLevel) {
                case "NONE":
                    guildBoostLevelString = "None";
                    break;
                case "TIER_1":
                    guildBoostLevelString = "Level 1";
                    break;
                case "TIER_2":
                    guildBoostLevelString = "Level 2";
                    break;
                case "TIER_3":
                    guildBoostLevelString = "Level 3";
                    break;
                default:
                    guildBoostLevelString = "Unknown";
                    break;
            }

            long creationDateEpoch = server.getCreationTimestamp().getEpochSecond();
            String creationDate = DiscordTimestamp.getTimestamp(Format.SHORT_DATE_TIME, creationDateEpoch);

            Optional<Instant> joinDateTimestamp = event.getApi().getYourself().getJoinedAtTimestamp(server); // The day the bot has joined, not the command executor
            long joinDateEpoch = joinDateTimestamp.isPresent() ? joinDateTimestamp.get().getEpochSecond() : 0;
            String joinDate = joinDateEpoch != 0 ? DiscordTimestamp.getTimestamp(Format.SHORT_DATE_TIME, joinDateEpoch) : "Hasn't joined this guild";
            
            EmbedBuilder embed = new EmbedBuilder()
                .setTitle(guildName + " info")
                .addField(":id: ID", Markdown.code(guildId), true)
                .addField(":crown: Owner", Markdown.code(guildOwner), true)
                .addField(":earth_americas: Region", guildRegion, true)
                .addField(":sparkles: " + guildBoostCount + " Boosters", Markdown.bold("Level: ") + guildBoostLevelString, true)
                .addField(":speech_balloon: " + guildChannels + " Channels", Markdown.bold("Voice: ") + guildVoiceChannels + "\n" + Markdown.bold("Text: ") + guildTextChannels + "" , true)
                .addField(":busts_in_silhouette: " + guildMembers + " Members", Markdown.bold("Online: ") + guildOnlineMembers + "\n" + Markdown.bold("Offline: ") + guildOfflineMembers + "", true)
                .addField(":shield: " + String.valueOf(guildRoles) + " Roles", Markdown.bold("Highest: ") + String.valueOf(highestRole), true)
                .addField(":calendar: Creation Date", creationDate, true)
                .addField(":inbox_tray: Bot Join Date", joinDate, true)
                .setColor(PURPLE);
            
            if (guildIconString != null) {
                embed.setThumbnail(guildIconString);
            }
           
            User executorUser = event.getMessageAuthor().asUser().orElse(null);        
            String executorAvatarUrl = executorUser != null ? executorUser.getAvatar().getUrl().toString() : null;
            
            if (executorAvatarUrl != null) {
                embed.setFooter("Command executed by" + executor, executorAvatarUrl);
            } else {
                embed.setFooter("Command executed by" + executor);
            }

            event.getChannel().sendMessage(embed).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
        } else {
            String guildName = server.getName();
            String guildIconString = server.getIcon().get().getUrl().toString();
            List<Role> guildRoles = server.getRoles();
            String guildRolesString = "";

            for (Role role : guildRoles) {
                if (role.getName().equals("@everyone")) {
                    guildRoles.remove(role);
                } else {
                    guildRolesString = guildRolesString + " " + role.getMentionTag();
                }
            }

            if (guildRolesString.isEmpty()) {
                event.getChannel().sendMessage("This guild doesn't have any role").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
                return;
            }

            EmbedBuilder embed = new EmbedBuilder()
                .setTitle(guildName + " roles")
                .setDescription(guildRolesString)
                .setThumbnail(guildIconString)
                .setColor(PURPLE);

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
}