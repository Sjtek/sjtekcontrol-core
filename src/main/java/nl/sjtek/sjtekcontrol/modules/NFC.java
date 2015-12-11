package nl.sjtek.sjtekcontrol.modules;

import nl.sjtek.sjtekcontrol.network.ApiHandler;
import nl.sjtek.sjtekcontrol.network.Arguments;
import nl.sjtek.sjtekcontrol.settings.User;
import org.json.JSONObject;

/**
 * Created by wouter on 28-11-15.
 */
@SuppressWarnings("unused")
public class NFC extends BaseModule {

    public void read(Arguments arguments) {
        String scannedCard = arguments.getCardId();

        for (User user : User.values()) {
            for (String cardId : user.getNFCTags()) {
                if (cardId.equals(scannedCard)) doStuff(user);
            }
        }
    }

    private void doStuff(User user) {
        ApiHandler.getInstance().masterToggle(new Arguments().setUseVoice(true).setUser(user));
    }

    @Override
    public JSONObject toJson() {
        return new JSONObject();
    }

    @Override
    public String getSummaryText() {
        return "There is not much to tell about the NFC module.";
    }
}
