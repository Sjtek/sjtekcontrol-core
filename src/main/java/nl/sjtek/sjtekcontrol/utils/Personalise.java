package nl.sjtek.sjtekcontrol.utils;

import nl.sjtek.sjtekcontrol.settings.User;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by wouter on 28-11-15.
 */
public class Personalise {

    private static Random random = new Random();

    public static String messageWelcome(User user) {
        return getText("Welcome back, ", user.getGreetings(), user.getRandomNickname());
    }

    public static String messageLeave(User user) {
        return getText("Good bye, ", user.getFarewells(), user.getRandomNickname());
    }

    private static String getText(String defaultText, String[][] strings, String name) {
        if (strings.length == 4) {
            int length = strings[getRange()].length;
            String base = strings[getRange()][random.nextInt(length)];
            return String.format(base, name);
        } else {
            return defaultText + " " + name;
        }
    }

    private static int getRange() {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int range = 0;
        if (hour >= 0) range = 0;
        if (hour >= 6) range = 1;
        if (hour >= 12) range = 2;
        if (hour >= 18) range = 3;
        return range;
    }

}
