// TODO make so all of the getUserByName and by Discriminator works even if the user is not in the server
// TODO make so all of the embeds' footer containing "executed by" have that mini picture of the executor user

package me.eduardogottert;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;

import me.eduardogottert.commands.utils.*;
import me.eduardogottert.commands.utils.infos.*;
import me.eduardogottert.commands.utils.rngs.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class Main {
    private static Logger logger = LogManager.getLogger(Main.class);

    public final static String prefix = "!";
    public static String BOT_TOKEN;    
    public static void main(String[] args) {
        
        Properties prop = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            prop.load(input);
            BOT_TOKEN = prop.getProperty("BOT_TOKEN");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (BOT_TOKEN == null) {
            logger.fatal("BOT_TOKEN is null. Please check your config.properties file.");
            return;
        }

        DiscordApi api = new DiscordApiBuilder().setToken(BOT_TOKEN).setAllIntents().login().join();
        
        Collection<Server> guilds = api.getServers();
        
        logger.warn("Logged in as " + api.getYourself().getName());
        logger.info("Client ID (" + api.getClientId() + ")");
        logger.info(api.createBotInvite(Permissions.fromBitmask(8)));
        logger.info("Currently in " + guilds.size() + " guild(s): " + guilds);
        
        // RNG Commands
        api.addMessageCreateListener(new ChoiceCommand());
        api.addMessageCreateListener(new DiceCommand());
        api.addMessageCreateListener(new JankenponCommand());
        api.addMessageCreateListener(new RandomCommand());

        // Info Commands
        api.addMessageCreateListener(new AvatarCommand());
        api.addMessageCreateListener(new GuildBannerCommand());
        api.addMessageCreateListener(new GuildIconCommand());
        api.addMessageCreateListener(new UserBannerCommand());
        api.addMessageCreateListener(new UserInfoCommand());

        // Utils Commands
        api.addMessageCreateListener(new AboutCommand());
        api.addMessageCreateListener(new PingCommand());
        api.addMessageCreateListener(new PollCommand());
        api.addMessageCreateListener(new ShortenCommand());
        //todo Wheater Command, Time Command, Translate Command and more
    }

    public static boolean parseCommand(String[] aliases, String commandExecuted) {
        return parseCommand(aliases, commandExecuted, true);
    }

    public static boolean parseCommand(String[] aliases, String commandExecuted, boolean isCommand) {
        for (String element : aliases) {
            if ((isCommand && commandExecuted.equalsIgnoreCase(prefix + element)) ||
             (!isCommand && commandExecuted.equalsIgnoreCase(element))) {
                return true;
            }
        }
        return false;
    }
}