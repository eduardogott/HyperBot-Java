package me.eduardogottert.commands.utils.infos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.embed.EmbedBuilder;
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

public class GuildBannerCommand implements MessageCreateListener {

    private static Logger logger = LogManager.getLogger(GuildBannerCommand.class);
    private static String[] aliases = {"guildbanner", "serverbanner"};
    private static final Color PURPLE = new Color(128, 0, 128);

    private static String getServerBanner(MessageCreateEvent event, String serverId) {
        HttpClient client = HttpClient.newHttpClient();
        final String[] finalBannerUrl = new String[1];

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://discord.com/api/v10/guilds/" + serverId))
            .header("Authorization", "Bot " + Main.BOT_TOKEN)
            .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenAccept(body -> {
            JSONObject serverJson = new JSONObject(body);
            String bannerHash = serverJson.getString("banner");
            finalBannerUrl[0] = (bannerHash != null) ? "https://cdn.discordapp.com/banners/" + serverId + "/" + bannerHash + ".png?size=512" : null;
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

        String guildId = event.getMessage().getChannel().asServerTextChannel().get().getServer().getIdAsString();
        String bannerUrl = getServerBanner(event, guildId);
        
        if (bannerUrl == null) {
            event.getChannel().sendMessage("This guild does not have a banner").exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
            return;
        }

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle(event.getMessage().getChannel().asServerTextChannel().get().getServer().getName() + " banner")
            .setImage(bannerUrl)
            .setColor(PURPLE)
            .setFooter("Command executed by " + executor);

        event.getChannel().sendMessage(embed).exceptionally(ExceptionLogger.get(MissingPermissionsException.class));
    }
}