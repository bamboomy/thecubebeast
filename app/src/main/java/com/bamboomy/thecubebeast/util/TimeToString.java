package com.bamboomy.thecubebeast.util;

public class TimeToString {

    public static String convert(long difference) {

        if (difference == 9999000) {
            return "99:59.99";
        }

        long minutes = difference / 60000;

        difference -= minutes * 60000;

        long seconds = difference / 1000;

        difference -= seconds * 1000;

        long hundreds = difference / 10;

        String timeText = String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds) + "."
                + String.format("%02d", hundreds);

        return timeText;
    }

}
