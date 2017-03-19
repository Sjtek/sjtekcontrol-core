package nl.sjtek.control.core.modules;

import com.google.common.eventbus.Subscribe;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.events.DataChangedEvent;
import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.core.utils.LightThread;
import nl.sjtek.control.data.responses.MusicResponse;
import nl.sjtek.control.data.responses.Response;

/**
 * Created by wouter on 19-3-17.
 */
public class Art extends BaseModule {

    private String previousUrl = "";
    private int lights[];

    public Art(String key) {
        super(key);
        Bus.regsiter(this);
    }

    @Subscribe
    public void onUpdate(DataChangedEvent event) {
        if (event.getKey().equals("music")) {
            MusicResponse musicResponse = (MusicResponse) event.getResponse();
            String url = musicResponse.getSong().getAlbumArt();
            if (url.isEmpty()) url = musicResponse.getSong().getArtistArt();
            if (!url.isEmpty() && !url.equals(previousUrl) && lights != null) {
                previousUrl = url;
                new LightThread(url, lights).start();
            }
        }
    }

    public void enable(Arguments arguments) {
        this.lights = arguments.getLights();
        if (previousUrl != null && !previousUrl.isEmpty()) {
            new LightThread(previousUrl, this.lights);
        }
    }

    @Override
    public Response getResponse() {
        return null;
    }

    @Override
    public String getSummaryText() {
        return "";
    }

    @Override
    public boolean isEnabled(String user) {
        return false;
    }
}
