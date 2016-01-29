package nl.sjtek.control.core.modules;

import nl.sjtek.control.core.network.ApiHandler;
import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.core.settings.SettingsManager;
import nl.sjtek.control.data.responses.NFCResponse;
import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.settings.User;

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
    public Response getResponse() {
        return new NFCResponse();
    }

    @Override
    public String getSummaryText() {
        return "There is not much to tell about the NFC module.";
    }
}
