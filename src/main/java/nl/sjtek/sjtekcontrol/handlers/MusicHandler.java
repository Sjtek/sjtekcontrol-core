package nl.sjtek.sjtekcontrol.handlers;

import nl.sjtek.sjtekcontrol.data.Response;
import nl.sjtek.sjtekcontrol.utils.Speech;
import org.bff.javampd.MPD;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDPlaylistException;
import org.bff.javampd.objects.MPDSong;
import org.json.JSONObject;

import java.net.UnknownHostException;

public class MusicHandler extends SjtekHandler {

    public static final String CONTEXT = "/api/music";

    private MPD mpd;
    private Response response;

    public MusicHandler() throws UnknownHostException, MPDConnectionException {
        MPD.Builder builder = new MPD.Builder();
        builder.server("127.0.0.1");
        builder.port(6600);
        this.mpd = builder.build();
    }

    public Response execute(String path[], boolean useVoice) {
        response = new Response();

        try {
            if ("toggle".equals(path[3])) toggle(useVoice);
            else if ("play".equals(path[3])) play(useVoice);
            else if ("pause".equals(path[3])) pause(useVoice);
            else if ("stop".equals(path[3])) stop(useVoice);
            else if ("next".equals(path[3])) next(useVoice);
            else if ("previous".equals(path[3])) previous(useVoice);
            else if ("rewind".equals(path[3])) rewind(useVoice);
            else if ("shuffle".equals(path[3])) shuffle(useVoice);
            else if ("clear".equals(path[3])) clear(useVoice);
            else if ("current".equals(path[3])) current(useVoice);
            else if ("info".equals(path[3])) info(useVoice);
            else if ("volume".equals(path[3])) {
                if ("lower".equals(path[4])) volumeLower(useVoice);
                else if ("raise".equals(path[4])) volumeRaise(useVoice);
                else if ("neutral".equals(path[4])) volumeNeutral(useVoice);
            } else {
                response.setCode(404);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            response.setCode(404);
        } catch (MPDPlayerException | MPDPlaylistException e) {
            response.setCode(500);
            response.put("error", e.getMessage());
        }

        return response;
    }

    private void toggle(boolean useVoice) {
        response.setCode(404);
    }

    private void play(boolean useVoice) throws MPDPlayerException {
        mpd.getPlayer().play();
    }

    private void pause(boolean useVoice) throws MPDPlayerException {
        mpd.getPlayer().pause();
    }

    private void stop(boolean useVoice) throws MPDPlayerException {
        mpd.getPlayer().stop();
    }

    private void next(boolean useVoice) throws MPDPlayerException {
        mpd.getPlayer().playNext();
    }

    private void previous(boolean useVoice) throws MPDPlayerException {
        mpd.getPlayer().playPrev();
    }

    private void rewind(boolean useVoice) {
        response.setCode(404);
    }

    private void shuffle(boolean useVoice) throws MPDPlaylistException {
        mpd.getPlaylist().shuffle();
    }

    private void clear(boolean useVoice) throws MPDPlaylistException {
        mpd.getPlaylist().clearPlaylist();
    }

    private void current(boolean useVoice) throws MPDPlayerException {
        JSONObject jsonObject = new JSONObject();
        MPDSong song = mpd.getPlayer().getCurrentSong();
        jsonObject.put("artist", song.getArtistName());
        jsonObject.put("title", song.getTitle());
        jsonObject.put("album", song.getAlbumName());
        response.put("song", jsonObject);
        if (useVoice)
            Speech.speechAsync("The current playing song is " + song.getTitle() + " by " + song.getArtistName() + ".");
    }

    private void info(boolean useVoice) throws MPDPlayerException {
        current(useVoice);
        getVolume(useVoice);
    }

    private void setVolume(int value) throws MPDPlayerException {
        mpd.getPlayer().setVolume(value);
    }

    private int getVolume(boolean useVoice) throws MPDPlayerException {
        int volume = mpd.getPlayer().getVolume();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("music", volume);
        response.put("volume", jsonObject);
        if (useVoice) Speech.speechAsync("The volume of the music is" + volume + "%.");
        return volume;
    }

    private void volumeLower(boolean useVoice) throws MPDPlayerException {
        int newVolume = mpd.getPlayer().getVolume() - 3;
        if (newVolume < 0) newVolume = 0;
        setVolume(newVolume);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("music", newVolume);
        response.put("volume", jsonObject);
    }

    private void volumeRaise(boolean useVoice) throws MPDPlayerException {
        int newVolume = mpd.getPlayer().getVolume() + 3;
        if (newVolume > 100) newVolume = 100;
        setVolume(newVolume);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("music", newVolume);
        response.put("volume", jsonObject);
    }

    private void volumeNeutral(boolean useVoice) throws MPDPlayerException {
        setVolume(20);
    }
}
