package me.eduardogottert.discordUtils;

public class Markdown {
    public static String bold(String text) {
        return "**" + text + "**";
    }

    public static String italic(String text) {
        return "*" + text + "*";
    }

    public static String underline(String text) {
        return "__" + text + "__";
    }

    public static String strikeThrough(String text) {
        return "~~" + text + "~~";
    }

    public static String code(String text) {
        return "`" + text + "`";
    }

    public static String codeBlock(String text) {
        return "```" + text + "```";
    }

    public static String codeBlock(String language, String text) {
        return "```" + language + "\n" + text + "```";
    }

    public static String quote(String text) {
        return ">" + text;
    }

    public static String boldItalic(String text) {
        return "***" + text + "***";
    }

    public static String header(int level, String text) {
        return "#".repeat((level <= 3 && level >= 1) ? level : 1) + " " + text;
    }

    public static String link(String text, String url) {
        return "[" + text + "](" + url + ")";
    }

    public static String listItem(String text) {
        return "- " + text;
    }

    public static String listItem(String text, int indent) {
        return " ".repeat(indent) + "- " + text;
    }

    public static String numberedListItem(int number, String text) {
        return number + ". " + text;
    }

}
