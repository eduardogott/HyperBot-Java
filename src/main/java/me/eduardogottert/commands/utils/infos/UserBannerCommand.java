package me.eduardogottert.commands.utils.infos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.exception.MissingPermissionsException;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.util.logging.ExceptionLogger;
import org.json.JSONObject;
import java.awt.Color;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import me.eduardogottert.Main;

public class UserBannerCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(UserBannerCommand.class);
    private static String[] aliases = {"userbanner", "banner"};
    private static final Color PURPLE = new Color(128, 0, 128);

    private static String getUserBanner(MessageCreateEvent event, String userId) {
        HttpClient client = HttpClient.newHttpClient();
        final String[] finalBannerUrl = new String[1]; // Declare a mutable array to hold the finalBannerUrl value

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://discord.com/api/v10/users/" + userId))
            .header("Authorization", "Bot " + Main.BOT_TOKEN)
            .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenAccept(body -> {
            JSONObject userJson = new JSONObject(body);
            String bannerHash = userJson.getString("banner");
            finalBannerUrl[0] = (bannerHash != null) ? "https://cdn.discordapp.com/banners/" + userId + "/" + bannerHash + ".png?size=512" : null; // Assign the value to the mutable array
        })
        .join();

        return finalBannerUrl[0];
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
        logger.info("Command '" + content + "' executed by " + executor + " at #" + channel);

        User user;

        if (splitCommand.length == 1) {
            String userToGetBanner = event.getMessageAuthor().getIdAsString();
            user = event.getApi().getUserById(userToGetBanner).join();
        } else {
            String userToGetBanner = splitCommand[1];
            
            if (userToGetBanner.startsWith("<@") && userToGetBanner.endsWith(">")) {
                String userId = userToGetBanner.substring(2, userToGetBanner.length() - 1);
                user = event.getApi().getUserById(userId).join();
            } else if (userToGetBanner.matches("\\d+")) {
                user = event.getApi().getUserById(userToGetBanner).join();
            } else {
                user = event.getApi().getCachedUserByDiscriminatedName(userToGetBanner).orElse(null);
                if (user == null) {
                    event.getChannel().sendMessage("Invalid user").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
                    return;
                }
            }
        }

        String bannerUrl = getUserBanner(event, user.getIdAsString());
        
        if (bannerUrl == null) {
            event.getChannel().sendMessage("User does not have a banner").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle(event.getMessageAuthor().asUser().toString() + "'s banner")
            .setImage(bannerUrl)
            .setColor(PURPLE)
            .setFooter("Command executed by " + executor);

        event.getChannel().sendMessage(embed).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
    }
}