package me.eduardogottert.discordUtils;

import java.time.Instant;

public class DiscordTimestamp {
    public static enum Format {
        DEFAULT(">"),
        SHORT_TIME(":t>"),
        LONG_TIME(":T>"),
        SHORT_DATE(":d>"),
        LONG_DATE(":D>"),
        SHORT_DATE_TIME(":f>"),
        LONG_DATE_TIME(":F>"),
        RELATIVE_TIME(":R>");

        public final String suffix;

        private Format(String suffix) {
            this.suffix = suffix;
        }
    }

    /**
     * Returns a formatted timestamp string based on the provided format and timestamp.
     *
     * @param format    the format of the timestamp string
     * @param timestamp the timestamp value in seconds
     * @return the formatted timestamp string
     */
    public static String getTimestamp(Format format, long timestamp) {
        if (timestamp > 2147483647) {
            return null;
        }

        return "<t:" + timestamp + format.suffix;
    }

    /**
     * Returns a formatted timestamp string based on system time.
     *
     * @param format    the format of the timestamp string
     * @return the formatted timestamp string
     */
    public static String getCurrentTimestamp(Format format) {
        return getTimestamp(format, Instant.now().getEpochSecond());
    }

    public static String getCurrentTimestamp() {
        return getTimestamp(Format.DEFAULT, Instant.now().getEpochSecond());
    }

    public static String getTimestamp(Format format, String timestamp) {
        if (timestamp.matches("\\d+")) {
            return getTimestamp(format, Long.parseLong(timestamp));
        }

        return null;
    }

    public static String getTimestamp() {
        return getTimestamp(Format.DEFAULT, Instant.now().getEpochSecond());
    }

    public static String getTimestamp(long timestamp) {
        return getTimestamp(Format.DEFAULT, timestamp);
    }

    public static String getTimestamp(Format format, Instant timestamp) {
        return getTimestamp(format, timestamp.getEpochSecond());
    }

    public static String getTimestamp(Instant timestamp) {
        return getTimestamp(Format.DEFAULT, timestamp.getEpochSecond());
    }
}
