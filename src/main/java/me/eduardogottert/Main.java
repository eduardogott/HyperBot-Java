package me.eduardogottert;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;

import me.eduardogottert.commands.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;

public class Main {
    private static Logger logger = LogManager.getLogger(Main.class);

    public final static String prefix = "!";
    private static final String BOT_TOKEN = "MTIxNzYyMDAzNTI4MDA0NDE2Mg.GXnd7N.yp-qMYe-fQfJfAXQnEvpMBV2--gNSQzZEqxasE";
    public static void main(String[] args) {
        
        DiscordApi api = new DiscordApiBuilder().setToken(BOT_TOKEN).setAllIntents().login().join();
        
        Collection<Server> guilds = api.getServers();
        
        logger.warn("Logged in as " + api.getYourself().getName());
        logger.info("Client ID (" + api.getClientId() + ")");
        logger.info(api.createBotInvite(Permissions.fromBitmask(8)));
        logger.info("Currently in " + guilds.size() + " guild(s): " + guilds);
        
        // Utils commands
        api.addMessageCreateListener(new AboutCommand());
        api.addMessageCreateListener(new ChoiceCommand());
        api.addMessageCreateListener(new DiceCommand());
        api.addMessageCreateListener(new JankenponCommand());
        api.addMessageCreateListener(new PingCommand());
        api.addMessageCreateListener(new PollCommand());
        api.addMessageCreateListener(new RandomCommand());
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