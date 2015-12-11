package nl.sjtek.sjtekcontrol.utils;

import nl.sjtek.sjtekcontrol.settings.User;

/**
 * Created by wouter on 28-11-15.
 */
public class Personalise {
    public static String messageWelcome(User user) {
        return "Welcome back, " + user.toString();
    }

    public static String messageLeave(User user) {
        return "Good bye, " + user.toString();
    }
}
