package nl.sjtek.sjtekcontrol.devices;

import nl.sjtek.sjtekcontrol.data.Arguments;
import nl.sjtek.sjtekcontrol.utils.Executor;
import org.bff.javampd.MPD;
import org.bff.javampd.MPDFile;
import org.bff.javampd.Player;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDPlaylistException;
import org.bff.javampd.objects.MPDSong;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;


public class Music {

    public static final String MPD_HOST = "192.168.0.64";
    public static final int MPD_PORT = 6600;
    public static final int VOLUME_STEP_UP = 3;
    public static final int VOLUME_STEP_DOWN = 3;
    public static final int VOLUME_NEUTRAL = 10;
    public static final String MPC_COMMAND = "/usr/bin/mpc";

    private MPD mpd = null;

    /**
     * Connect to an MPD server on {@link #MPD_HOST} with port {@link #MPD_PORT}.
     *
     * @throws UnknownHostException
     * @throws MPDConnectionException
     */
    public Music() throws UnknownHostException, MPDConnectionException {
        MPD.Builder builder = new MPD.Builder();
        builder.server(MPD_HOST);
        builder.port(MPD_PORT);
        mpd = builder.build();
    }

    /**
     * Toggle player from PLAY to PAUSE or PAUSE to PLAY or STOPPED to PLAY.
     *
     * @param arguments
     */
    public void toggle(Arguments arguments) {
        try {
            Player player = mpd.getPlayer();
            Player.Status status = player.getStatus();
            if (status == Player.Status.STATUS_STOPPED) {
                player.play();
            } else {
                player.pause();
            }
        } catch (MPDPlayerException ignored) {

        }
    }

    public void rave(Arguments arguments) {
        play(new Arguments("url=spotify:track:3QKv87XsylJWvTCzssDvnr"));
    }

    /**
     * Toggle player to PLAY.<br>
     * If an URL is specified it will insert this after the current playing song and start it.
     *
     * @param arguments Uses URL
     */
    public void play(Arguments arguments) {
        String url = arguments.getUrl();
        if (url != null) {
            try {
                String command =
                        MPC_COMMAND + " -h " + MPD_HOST + " insert " +
                                (arguments.getStreamType() == Arguments.StreamType.YouTube ? "yt:" : "") + url;
                Executor.execute(command);
                mpd.getPlayer().playNext();
                mpd.getPlayer().play();
            } catch (IOException | InterruptedException | MPDPlayerException e) {
                e.printStackTrace();
            }
        } else {

            try {
                mpd.getPlayer().play();
            } catch (MPDPlayerException ignored) {

            }
        }
    }

    /**
     * Toggle player to PAUSE if it is PLAY.
     *
     * @param arguments
     */
    public void pause(Arguments arguments) {
        try {
            Player player = mpd.getPlayer();
            Player.Status status = player.getStatus();
            if (status == Player.Status.STATUS_PLAYING) {
                player.pause();
            }
        } catch (MPDPlayerException ignored) {

        }
    }

    /**
     * Toggle player to STOP.
     *
     * @param arguments
     */
    public void stop(Arguments arguments) {
        try {
            if (mpd.getPlayer().getStatus() != Player.Status.STATUS_STOPPED) {
                mpd.getPlayer().stop();
            } else {
                clear(new Arguments());
            }
        } catch (MPDPlayerException ignored) {
        }
    }

    /**
     * Go to the next song in the queue.
     *
     * @param arguments
     */
    public void next(Arguments arguments) {
        try {
            mpd.getPlayer().playNext();
        } catch (MPDPlayerException ignored) {

        }
    }

    /**
     * Go to the previous song in the queue.
     *
     * @param arguments
     */
    public void previous(Arguments arguments) {
        try {
            mpd.getPlayer().playPrev();
        } catch (MPDPlayerException ignored) {

        }
    }

    /**
     * Shuffle the queue. This will not stop playback.
     *
     * @param arguments
     */
    public void shuffle(Arguments arguments) {
        try {
            mpd.getPlaylist().shuffle();
        } catch (MPDPlaylistException ignored) {

        }
    }

    /**
     * Clear the queue. This wil set the player to STOP.
     *
     * @param arguments
     */
    public void clear(Arguments arguments) {
        try {
            mpd.getPlaylist().clearPlaylist();
        } catch (MPDPlaylistException ignored) {
        }
    }

    /**
     * Empty.
     *
     * @param arguments
     */
    public void current(Arguments arguments) {

    }

    /**
     * Empty.
     *
     * @param arguments
     */
    public void info(Arguments arguments) {

    }

    /**
     * Clear queue and stop player. Then add SjtekSjpeellijst and Taylor Swift, shuffle it and start playback.
     *
     * @param arguments
     */
    public void start(Arguments arguments) {
        Arguments dummyArguments = new Arguments();
        clear(dummyArguments);
        volumeneutral(dummyArguments);

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

        shuffle(dummyArguments);
        play(dummyArguments);
    }

    /**
     * Lower the volume with {@link #VOLUME_STEP_DOWN}.
     *
     * @param arguments
     */
    public void volumelower(Arguments arguments) {
        try {
            int newVolume = mpd.getPlayer().getVolume() - VOLUME_STEP_DOWN;
            if (newVolume < 0) newVolume = 0;
            mpd.getPlayer().setVolume(newVolume);
        } catch (MPDPlayerException ignored) {
        }
    }

    /**
     * Raise the volume with {@link #VOLUME_STEP_UP}.
     *
     * @param arguments
     */
    public void volumeraise(Arguments arguments) {
        try {
            int newVolume = mpd.getPlayer().getVolume() + VOLUME_STEP_UP;
            if (newVolume > 100) newVolume = 100;
            mpd.getPlayer().setVolume(newVolume);
        } catch (MPDPlayerException ignored) {

        }
    }

    /**
     * Set the volume to {@link #VOLUME_NEUTRAL}.
     *
     * @param arguments
     */
    public void volumeneutral(Arguments arguments) {
        try {
            mpd.getPlayer().setVolume(VOLUME_NEUTRAL);
        } catch (MPDPlayerException ignored) {

        }
    }

    public boolean isPlaying() {
        try {
            if (mpd.getPlayer().getStatus() == Player.Status.STATUS_PLAYING) {
                return true;
            } else {
                return false;
            }
        } catch (MPDPlayerException e) {
            return false;
        }
    }

    private Player.Status getPlayerState() {
        try {
            return mpd.getPlayer().getStatus();
        } catch (MPDPlayerException e) {
            return null;
        }
    }

    private JSONObject getCurrentSong() {
        JSONObject jsonSong = new JSONObject();

        if (getPlayerState() != Player.Status.STATUS_PLAYING && getPlayerState() != Player.Status.STATUS_PAUSED) {
            jsonSong = null;
        } else {
            try {
                Player player = mpd.getPlayer();
                MPDSong song = player.getCurrentSong();
                jsonSong.put("artist", song.getArtistName());
                jsonSong.put("album", song.getAlbumName());
                jsonSong.put("title", song.getName());
                jsonSong.put("total", player.getTotalTime());
                jsonSong.put("elapsed", player.getElapsedTime());
            } catch (MPDPlayerException e) {
                jsonSong = null;
            }
        }

        if (jsonSong == null) {
            jsonSong = new JSONObject();
            jsonSong.put("artist", "");
            jsonSong.put("title", "");
            jsonSong.put("album", "");
            jsonSong.put("total", 0);
            jsonSong.put("elapsed", 0);
        }

        return jsonSong;
    }

    private int getVolume() {
        try {
            return mpd.getPlayer().getVolume();
        } catch (MPDPlayerException e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("state", (getPlayerState() == null ? "ERROR" : getPlayerState().toString()));
        jsonObject.put("song", getCurrentSong());
        jsonObject.put("volume", getVolume());
        return jsonObject.toString();
    }
}