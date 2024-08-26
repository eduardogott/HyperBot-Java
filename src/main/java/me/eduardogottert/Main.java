// TODO make so all of the getUserByName and by Discriminator works even if the user is not in the server
// TODO do something with "igdb", "open weather api" and "fx rates"
// TODO make so all of the embeds' footer containing "executed by" have that mini picture of the executor user
// TODO uma roleta tipo wheel of names
// TODO language_files

//
package me.eduardogottert;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.message.Message;
import org.javacord.api.util.logging.ExceptionLogger;

import me.eduardogottert.commands.utils.*;
import me.eduardogottert.commands.utils.infos.*;
import me.eduardogottert.commands.utils.rngs.*;
import me.eduardogottert.commands.moderation.punishments.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import java.io.IOException;
//import java.io.InputStream;
//import java.util.Properties;
//import java.io.File;

public class Main {
    private static Logger logger = LogManager.getLogger(Main.class);
    public final static String prefix = "!";
    public static String LANGUAGE_FILE;
    public static String BOT_TOKEN;

    public static void main(String[] args) {
        logger.info("Starting bot...");

        /*Properties prop = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            logger.info("Parsing token from config.properties...");
            prop.load(input);
            BOT_TOKEN = prop.getProperty("BOT_TOKEN");
            LANGUAGE_FILE = prop.getProperty("LANGUAGE");
        } catch (IOException e) {
            e.printStackTrace();
            logger.fatal("Error while trying to read config.properties file.");
        }

        if (BOT_TOKEN == null) {
            logger.fatal("BOT_TOKEN is null. Please check your config.properties file.");
            return;
        }

        if (LANGUAGE_FILE == null) {
            if (checkForLanguageFile(LANGUAGE_FILE)) {
                logger.info(LANGUAGE_FILE + " found in resources. Using it.");
            } else {
                logger.warn(LANGUAGE_FILE + " not found in resources. Trying to default to messages_default.json (English) instead.");
                LANGUAGE_FILE = "messages_default.json";
                if (checkForLanguageFile(LANGUAGE_FILE)) {
                    logger.info(LANGUAGE_FILE + " found in resources. Using it.");
                } else {
                    logger.fatal(LANGUAGE_FILE + " not found in resources. Please check your config.properties file and/or reinstall the messages_default.json file.");
                    return;
                }
            }
        }*/

        logger.info("Connecting to Discord...");
        DiscordApi api = new DiscordApiBuilder().setToken(BOT_TOKEN).setAllIntents().login().join();
        
        Collection<Server> guilds = api.getServers();
        
        logger.warn("Logged in as " + api.getYourself().getName());
        logger.info("Client ID (" + api.getClientId() + ")");
        logger.info(api.createBotInvite(Permissions.fromBitmask(8)));
        logger.info("Currently in " + guilds.size() + " guild(s): " + guilds);
        
        logger.info("Loading commands...");

        //#region Utils
        // Info Commands 
        api.addMessageCreateListener(new AvatarCommand());
        api.addMessageCreateListener(new GuildBannerCommand());
        api.addMessageCreateListener(new GuildIconCommand());
        api.addMessageCreateListener(new GuildInfoCommand());
        api.addMessageCreateListener(new UserBannerCommand());
        api.addMessageCreateListener(new UserInfoCommand());
        
        // RNG Commands
        api.addMessageCreateListener(new ChoiceCommand());
        api.addMessageCreateListener(new DiceCommand());
        api.addMessageCreateListener(new JankenponCommand());
        api.addMessageCreateListener(new RandomCommand());
        
        // Other Utils Commands
        api.addMessageCreateListener(new AboutCommand());
        api.addMessageCreateListener(new PingCommand());
        api.addMessageCreateListener(new PollCommand());
        api.addMessageCreateListener(new SayCommand());
        api.addMessageCreateListener(new ShortenCommand());
        //todo Wheater Command, Time Command, Translate Command and more
        //todo idea: Add gui screen for first time setup
        //#endregion
        
        //#region Moderation
        api.addMessageCreateListener(new BanCommand());
        api.addMessageCreateListener(new KickCommand());
        //#endregion

        logger.info("Bot loaded successfully! (" + api.getMessageCreateListeners().size() + " commands loaded).");
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

    /*private static boolean checkForLanguageFile(String file) {
        File languageFile = new File("../resources/" + LANGUAGE_FILE);
        if (languageFile.exists()) {
            return true;
        }
        return false;
    }*/

    public static long parseTime(String time) {
        String regex = "(\\d+)([ms|s|m|h|d|w|mo|y])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(time);

        if (!matcher.matches()) {
            return -1;
        }

        String number = matcher.group(1);
        String timeUnit = matcher.group(2);

        if (number == null || timeUnit == null) {
            return -1;
        }

        long delay = Long.parseLong(number);

        if (delay < 0) {
            return -1;
        }

        delay = multiplyTime(delay, timeUnit);

        return delay;
    }

    public static long parseTime(String time, String returnUnit) {
        String regex = "(\\d+)([ms|s|m|h|d|w|mo|y])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(time);

        if (!matcher.matches()) {
            return -1;
        }

        String number = matcher.group(1);
        String timeUnit = matcher.group(2);

        if (number == null || timeUnit == null) {
            return -1;
        }

        long delay = Long.parseLong(number);

        if (delay < 0) {
            return -1;
        }

        delay = multiplyTime(delay, timeUnit);

        switch (returnUnit.toLowerCase()) {
            case "ms":
            case "milliseconds":
                break;
            case "s":
            case "seconds":
                delay /= 1000L;
                break;
            case "m":
            case "minutes":
                delay /= 60000L; // 1000 * 60
                break;
            case "h":
            case "hours":
                delay /= 3600000L; // 1000 * 60 * 60
                break;
            case "d":
            case "days":
                delay /= 86400000L; // 1000 * 60 * 60 * 24
                break;
            case "w":
            case "weeks":
                delay /= 604800000L; // 1000 * 60 * 60 * 24 * 7
                break;
            case "mo":
            case "months":
                delay /= 2628000000L; // 1000 * 60 * 60 * 24 * 30
                break;
            case "y":
            case "years":
                delay /= 31536000000L; // 1000 * 60 * 60 * 24 * 365
                break;
            default:
                break;
        }

        return delay;
    }

    public static long multiplyTime(long delay, String timeUnit) {
        switch (timeUnit.toLowerCase()) {
            case "ms":
                break;
            case "s":
                delay *= 1000L;
                break;
            case "m":
                delay *= 60000L; // 1000 * 60
                break;
            case "h":
                delay *= 3600000L; // 1000 * 60 * 60
                break;
            case "d":
                delay *= 86400000L; // 1000 * 60 * 60 * 24
                break;
            case "w":
                delay *= 604800000L; // 1000 * 60 * 60 * 24 * 7
                break;
            case "mo":
                delay *= 2628000000L; // 1000 * 60 * 60 * 24 * 30
                break;
            case "y":
                delay *= 31536000000L; // 1000 * 60 * 60 * 24 * 365
                break;
            default:
                break;
        }

        return delay;
    }

    public static void deleteAfter(Message message, int delay, String timeUnit) {
        String messageContent = message.getContent();
        String messageAuthor = message.getAuthor().getName();
        long messageAuthorId = message.getAuthor().getId();
        String messageChannel = message.getChannel().asServerTextChannel().isPresent() ? message.getChannel().asServerTextChannel().get().getName() : "DMs";

        String messageDeleteLogText = "Message '" + messageContent + "' by " + messageAuthor + " (" + messageAuthorId + ") at #" + messageChannel + ".";

        switch (timeUnit.toLowerCase()) {
            case "milliseconds":
            case "ms":               
                break;
            case "seconds":
            case "s":
                delay *= 1000;
                break;
            case "minutes":
            case "m":
                delay *= 60000;
                break;
            case "hours":
            case "h":
                delay *= 3600000;
                break;
            default:
                break;
        }

        if (timeUnit.equalsIgnoreCase("seconds")) {
            delay *= 1000;
        }

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.warn("Failed to delete message (deleteAfter). (" + messageDeleteLogText + ")");
        }
        
        message.delete("Message deleted after " + delay + " " + timeUnit + " of being sent.").exceptionally(ExceptionLogger.get());
        logger.info("Deleted message (" + messageDeleteLogText + ").");
    }
}