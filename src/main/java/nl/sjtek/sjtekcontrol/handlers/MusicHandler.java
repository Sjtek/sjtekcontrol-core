package nl.sjtek.sjtekcontrol.handlers;

import nl.sjtek.sjtekcontrol.data.Response;
import nl.sjtek.sjtekcontrol.utils.Speech;
import org.bff.javampd.Database;
import org.bff.javampd.MPD;
import org.bff.javampd.MPDFile;
import org.bff.javampd.Player;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDDatabaseException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDPlaylistException;
import org.bff.javampd.objects.MPDSong;
import org.json.JSONObject;

import java.net.UnknownHostException;

public class MusicHandler extends SjtekHandler {

    public static final String CONTEXT = "/api/music";

    private static MPD mpd = null;
    private Response response;

    public MusicHandler() throws UnknownHostException, MPDConnectionException {
        MPD.Builder builder = new MPD.Builder();
        builder.server("192.168.0.64");
        builder.port(6600);
        if (mpd == null) mpd = builder.build();
    }

    public Response execute(String path[], boolean useVoice) {
        response = new Response();

        try {
            if ("toggle".equals(path[3])) toggle(useVoice);
            else if ("play".equals(path[3])) play(useVoice);
//            else if ("pause".equals(path[3])) pause(useVoice);
            else if ("stop".equals(path[3])) stop(useVoice);
            else if ("next".equals(path[3])) next(useVoice);
            else if ("previous".equals(path[3])) previous(useVoice);
//            else if ("rewind".equals(path[3])) rewind(useVoice);
            else if ("shuffle".equals(path[3])) shuffle(useVoice);
            else if ("clear".equals(path[3])) clear(useVoice);
            else if ("current".equals(path[3])) current(useVoice);
            else if ("info".equals(path[3])) info(useVoice);
            else if ("start".equals(path[3])) start(useVoice);
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
            response.setError(e.getMessage());
        }

        return response;
    }

    private void toggle(boolean useVoice) throws MPDPlayerException {
        mpd.getPlayer().pause();
        addCurrentSong();
    }

    private void play(boolean useVoice) throws MPDPlayerException {
        mpd.getPlayer().play();
        addCurrentSong();
    }

    private void stop(boolean useVoice) throws MPDPlayerException {
        mpd.getPlayer().stop();
        addCurrentSong();
    }

    private void next(boolean useVoice) throws MPDPlayerException {
        mpd.getPlayer().playNext();
        addCurrentSong();
    }

    private void previous(boolean useVoice) throws MPDPlayerException {
        mpd.getPlayer().playPrev();
        addCurrentSong();
    }

    private void shuffle(boolean useVoice) throws MPDPlaylistException, MPDPlayerException {
        mpd.getPlaylist().shuffle();
    }

    private void clear(boolean useVoice) {
        try {
            mpd.getPlaylist().clearPlaylist();
        } catch (MPDPlaylistException e) {
            e.printStackTrace();
        }
    }

    private void current(boolean useVoice) throws MPDPlayerException {
        addCurrentSong();
        MPDSong song = mpd.getPlayer().getCurrentSong();
        if (useVoice)
            Speech.speechAsync("The current playing song is " + song.getTitle() + " by " + song.getArtistName() + ".");
    }

    private void info(boolean useVoice) throws MPDPlayerException {
        current(useVoice);
        getVolume(useVoice);
    }

    private void start(boolean useVoice) throws MPDPlaylistException, MPDPlayerException {
        clear(false);
        volumeNeutral(false);
        try {
            mpd.getPlaylist().loadPlaylist("StjekSjpeellijst");
        } catch (MPDPlaylistException e) {
            e.printStackTrace();
        }
        MPDFile tt = new MPDFile();
        tt.setPath("Local media/Taylor Swift");
        try {
            mpd.getPlaylist().addFileOrDirectory(tt);
        } catch (MPDPlaylistException e) {
            e.printStackTrace();
        }

        try {
            shuffle(false);
        } catch (MPDPlaylistException | MPDPlayerException e) {
            e.printStackTrace();
        }
        play(false);
    }

    private void setVolume(int value) throws MPDPlayerException {
        mpd.getPlayer().setVolume(value);
        addVolume();
    }

    private int getVolume(boolean useVoice) throws MPDPlayerException {
        int volume = mpd.getPlayer().getVolume();
        addVolume();
        if (useVoice) Speech.speechAsync("The volume of the music is" + volume + "%.");
        return volume;
    }

    private void volumeLower(boolean useVoice) throws MPDPlayerException {
        int newVolume = mpd.getPlayer().getVolume() - 3;
        if (newVolume < 0) newVolume = 0;
        setVolume(newVolume);
    }

    private void volumeRaise(boolean useVoice) throws MPDPlayerException {
        int newVolume = mpd.getPlayer().getVolume() + 3;
        if (newVolume > 100) newVolume = 100;
        setVolume(newVolume);
    }

    private void volumeNeutral(boolean useVoice) throws MPDPlayerException {
        setVolume(20);
    }

    private void addCurrentSong() throws MPDPlayerException {
        JSONObject jsonObject = new JSONObject();
        Player player = mpd.getPlayer();
        MPDSong song = player.getCurrentSong();
        jsonObject.put("artist", song.getArtistName());
        jsonObject.put("title", song.getTitle());
        jsonObject.put("album", song.getAlbumName());
        response.setSong(jsonObject);
    }

    private void addVolume() throws MPDPlayerException {
        int volume = mpd.getPlayer().getVolume();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("music", volume);
        response.setVolume(jsonObject);
    }
}
