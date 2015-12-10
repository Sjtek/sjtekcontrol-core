package nl.sjtek.sjtekcontrol.utils;

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
