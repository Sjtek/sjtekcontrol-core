package nl.sjtek.sjtekcontrol.handlers;

import nl.sjtek.sjtekcontrol.data.Response;
import nl.sjtek.sjtekcontrol.utils.Speech;
import org.bff.javampd.exception.MPDConnectionException;

import java.net.UnknownHostException;

/**
 * Created by wouter on 18-6-15.
 */
public class RootHandler extends SjtekHandler {

    public static final String CONTEXT = "/api";
    private Response response;

    @Override
    public Response execute(String[] path, boolean useVoice) {
        response = new Response();
        if ("info".equals(path[2])) {
            addInfo();
        } else if ("switch".equals(path[2])) {
            masterSwitch(useVoice);
        } else {
            response.setCode(404);
        }

        return response;
    }

    public void masterSwitch(boolean useVoice) {
        MusicHandler musicHandler = null;
        try {
            musicHandler = new MusicHandler();
        } catch (UnknownHostException | MPDConnectionException e) {
            e.printStackTrace();
            response.setError(e.getMessage());
        }

        LightsHandler lightsHandler = new LightsHandler();

        boolean states[] = LightsHandler.getStates();
        boolean on = false;
        if (states[1]) on = true;
        if (states[2]) on = true;

        if (on) {
            response.combine(lightsHandler.execute("/api/lights/off".split("/"), false));
            if (musicHandler != null) {
                response.combine(musicHandler.execute("/api/music/stop".split("/"), false));
            }
            if (useVoice) Speech.speechAsync("Goodbye sir!");
        } else {
            response.combine(lightsHandler.execute("/api/lights/on".split("/"), false));
            if (musicHandler != null) {
                response.combine(musicHandler.execute("/api/music/start".split("/"), false));
            }
            if (useVoice) Speech.speechAsync("Welcome back sir! I will start the best playlist in the world.");

        }
    }

    public void addInfo() {
        MusicHandler musicHandler = null;
        try {
            musicHandler = new MusicHandler();
        } catch (UnknownHostException | MPDConnectionException e) {
            e.printStackTrace();
            response.setError(e.getMessage());
        }

        LightsHandler lightsHandler = new LightsHandler();
        if (musicHandler != null) {
            response.combine(musicHandler.execute("/api/music/info".split("/"), false));

        }
        response.combine(lightsHandler.execute("/api/lights/info".split("/"), false));
    }
}
