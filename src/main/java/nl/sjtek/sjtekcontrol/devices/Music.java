package nl.sjtek.sjtekcontrol.devices;

import nl.sjtek.sjtekcontrol.data.Arguments;
import nl.sjtek.sjtekcontrol.data.SettingsManager;
import org.bff.javampd.MPD;
import org.bff.javampd.MPDFile;
import org.bff.javampd.Player;
import org.bff.javampd.StandAloneMonitor;
import org.bff.javampd.events.*;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDPlaylistException;
import org.bff.javampd.objects.MPDSong;
import org.json.JSONObject;

import java.net.UnknownHostException;


@SuppressWarnings({"UnusedParameters", "unused"})
public class Music implements TrackPositionChangeListener, VolumeChangeListener, PlayerBasicChangeListener {

    private MPD mpd = null;
    private MusicState musicState = new MusicState();

    /**
     * Connect to an MPD server on an host with port port.
     *
     * @throws UnknownHostException
     * @throws MPDConnectionException
     */
    public Music() throws UnknownHostException, MPDConnectionException {
        MPD.Builder builder = new MPD.Builder();
        builder.server(SettingsManager.getInstance().getMusic().getMpdHost());
        builder.port(SettingsManager.getInstance().getMusic().getMpdPort());
        mpd = builder.build();

        StandAloneMonitor monitor = mpd.getMonitor();
        monitor.addVolumeChangeListener(this);
        monitor.addPlayerChangeListener(this);
        monitor.addTrackPositionChangeListener(this);
        monitor.start();
    }

    /**
     * Toggle player from PLAY to PAUSE or PAUSE to PLAY or STOPPED to PLAY.
     *
     * @param arguments Arguments
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

    /**
     * Toggle player to PLAY.<br>
     * If an URL is specified it will insert this after the current playing song and start it.
     *
     * @param arguments Uses URL
     */
    public void play(Arguments arguments) {
        int length;
        try {
            length = mpd.getPlaylist().getSongList().size();
        } catch (MPDPlaylistException e) {
            length = -1;
        }

        if (length == 0) {
            start(new Arguments());
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
     * @param arguments Arguments
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
     * @param arguments Arguments
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
     * @param arguments Arguments
     */
    public void next(Arguments arguments) {
        if (arguments.getUrl() == null) {
            try {
                mpd.getPlayer().playNext();
            } catch (MPDPlayerException ignored) {

            }
        } else {
            try {
                MPDFile mpdFile = new MPDFile();
                mpdFile.setPath(arguments.getUrl());
                mpd.getPlaylist().addFileOrDirectory(mpdFile);
                mpd.getPlayer().playNext();
            } catch (MPDPlaylistException | MPDPlayerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Go to the previous song in the queue.
     *
     * @param arguments Arguments
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
     * @param arguments Arguments
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
     * @param arguments Arguments
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
     * @param arguments Arguments
     */
    public void current(Arguments arguments) {

    }

    /**
     * Empty.
     *
     * @param arguments Arguments
     */
    public void info(Arguments arguments) {

    }

    /**
     * Clear queue and stop player. Then add SjtekSjpeellijst and Taylor Swift, shuffle it and start playback.
     *
     * @param arguments Arguments
     */
    public void start(Arguments arguments) {
        Arguments dummyArguments = new Arguments();
        clear(dummyArguments);
        volumeneutral(dummyArguments);

        String path;
        boolean injectTaylorSwift;
        if (arguments.getUser() != null) {
            path = arguments.getUser().getMusic();
            injectTaylorSwift = false;
        } else if (arguments.getUrl() != null) {
            path = arguments.getUrl();
            injectTaylorSwift = false;
        } else {
            path = SettingsManager.getInstance().getMusic().getDefaultPlaylist();
            injectTaylorSwift = SettingsManager.getInstance().getMusic().isTaylorSwiftInject();
        }

        try {
            MPDFile mpdFile = new MPDFile();
            mpdFile.setPath(path);
            mpd.getPlaylist().addFileOrDirectory(mpdFile);
        } catch (MPDPlaylistException e) {
            e.printStackTrace();
        }

        if (injectTaylorSwift) {
            MPDFile tt = new MPDFile();
            tt.setPath(SettingsManager.getInstance().getMusic().getTaylorSwiftPath());
            try {
                mpd.getPlaylist().addFileOrDirectory(tt);
            } catch (MPDPlaylistException e) {
                e.printStackTrace();
            }
        }

        shuffle(dummyArguments);
        play(dummyArguments);
    }

    /**
     * Lower the volume.
     *
     * @param arguments Arguments
     */
    public void volumelower(Arguments arguments) {
        try {
            int newVolume = mpd.getPlayer().getVolume() - SettingsManager.getInstance().getMusic().getVolumeStepDown();
            if (newVolume < 0) newVolume = 0;
            mpd.getPlayer().setVolume(newVolume);
        } catch (MPDPlayerException ignored) {
        }
    }

    /**
     * Raise the volume.
     *
     * @param arguments Arguments
     */
    public void volumeraise(Arguments arguments) {
        try {
            int newVolume = mpd.getPlayer().getVolume() + SettingsManager.getInstance().getMusic().getVolumeStepUp();
            if (newVolume > 100) newVolume = 100;
            mpd.getPlayer().setVolume(newVolume);
        } catch (MPDPlayerException ignored) {

        }
    }

    /**
     * Set the volume.
     *
     * @param arguments Arguments
     */
    public void volumeneutral(Arguments arguments) {
        try {
            mpd.getPlayer().setVolume(SettingsManager.getInstance().getMusic().getVolumeNeutral());
        } catch (MPDPlayerException ignored) {

        }
    }

    public boolean isPlaying() {
        try {
            return mpd.getPlayer().getStatus() == Player.Status.STATUS_PLAYING;
        } catch (MPDPlayerException e) {
            return false;
        }
    }


    @Override
    public String toString() {
        return musicState.toString();
    }

    @Override
    public void trackPositionChanged(TrackPositionChangeEvent event) {
        musicState.setTimeElapsed(event.getElapsedTime());
    }

    @Override
    public void volumeChanged(VolumeChangeEvent event) {
        musicState.setVolume(event.getVolume());
    }

    @Override
    public void playerBasicChange(PlayerBasicChangeEvent event) {
        try {
            Player player = mpd.getPlayer();
            MPDSong song = player.getCurrentSong();
            Player.Status status = player.getStatus();
            System.out.println("New player status: " + status.toString());
            if (status != Player.Status.STATUS_PLAYING && status != Player.Status.STATUS_PAUSED) {
                song = null;
                System.out.println("Song is null");
            }
            if (song != null) {
                musicState.setArtist(song.getArtistName());
                musicState.setAlbum(song.getAlbumName());
                musicState.setTitle(song.getName());
                musicState.setTimeTotal(player.getTotalTime());
                musicState.setStatus(status);
            } else {
                musicState.setArtist("");
                musicState.setAlbum("");
                musicState.setTitle("");
                musicState.setTimeTotal(0);
                musicState.setStatus(status);
            }
        } catch (MPDPlayerException e) {
            e.printStackTrace();
        }
    }

    public class MusicState {
        private String artist = "";
        private String title = "";
        private String album = "";
        private long timeTotal = 0;
        private long timeElapsed = 0;
        private int volume = -1;
        private String status = "ERROR";

        public void setArtist(String artist) {
            this.artist = artist;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public void setTimeTotal(long timeTotal) {
            this.timeTotal = timeTotal;
        }

        public void setTimeElapsed(long timeElapsed) {
            this.timeElapsed = timeElapsed;
        }

        public void setVolume(int volume) {
            this.volume = volume;
        }

        public void setStatus(Player.Status status) {
            this.status = (status != null ? status.toString() : "ERROR");
        }

        @Override
        public String toString() {
            JSONObject jsonSong = new JSONObject();
            jsonSong.put("artist", artist);
            jsonSong.put("title", title);
            jsonSong.put("album", album);
            jsonSong.put("total", timeTotal);
            jsonSong.put("elapsed", timeElapsed);

            JSONObject jsonMusic = new JSONObject();
            jsonMusic.put("song", jsonSong);
            jsonMusic.put("volume", volume);
            jsonMusic.put("state", status);

            return jsonMusic.toString();
        }
    }
}