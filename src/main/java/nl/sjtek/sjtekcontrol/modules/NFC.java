package nl.sjtek.sjtekcontrol.modules;

import nl.sjtek.sjtekcontrol.network.ApiHandler;
import nl.sjtek.sjtekcontrol.network.Arguments;
import nl.sjtek.sjtekcontrol.settings.SettingsManager;
import nl.sjtek.sjtekcontrol.settings.User;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by wouter on 28-11-15.
 */
@SuppressWarnings("unused")
public class NFC extends BaseModule {

    public void read(Arguments arguments) {
        for (Map.Entry<String, User> set : SettingsManager.getInstance().getUsers().entrySet()) {
            for (String cardId : set.getValue().getNfcTags()) {
                if (cardId.equals(arguments.getCardId())) doStuff(set.getValue());
            }
        }
    }

    public void doStuff(User user) {
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
