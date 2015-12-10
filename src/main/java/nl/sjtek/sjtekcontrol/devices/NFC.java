package nl.sjtek.sjtekcontrol.devices;

import nl.sjtek.sjtekcontrol.data.Arguments;
import nl.sjtek.sjtekcontrol.handlers.ApiHandler;
import nl.sjtek.sjtekcontrol.utils.User;

/**
 * Created by wouter on 28-11-15.
 */
@SuppressWarnings("unused")
public class NFC {

    private static final String[] CARDS_WOUTER = {
            "1853719819",       // OV Chipkaart
            "16514020840",      // Witte kaart
            "48115222160",      // Sleutelhanger
    };

    private static final String[] CARDS_TIJN = {
            "552518698",        // OV Chipkaart
            "6334480",
    };

    private static final String[] CARDS_KEVIN = {
            "281846010",
    };

    public void read(Arguments arguments) {
        String scannedCard = arguments.getCardId();

        for (String card : CARDS_WOUTER) {
            if (scannedCard.equals(card)) doStuff(User.WOUTER);
        }

        for (String card : CARDS_TIJN) {
            if (scannedCard.equals(card)) doStuff(User.TIJN);
        }

        for (String card : CARDS_KEVIN) {
            if (scannedCard.equals(card)) doStuff(User.KEVIN);
        }
    }

    private void doStuff(User user) {
        ApiHandler.getInstance().masterToggle(new Arguments().setUseVoice(true).setUser(user));
    }
}
