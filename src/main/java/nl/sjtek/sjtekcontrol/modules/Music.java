package nl.sjtek.sjtekcontrol.modules;

import nl.sjtek.sjtekcontrol.network.Arguments;
import nl.sjtek.sjtekcontrol.settings.SettingsManager;
import nl.sjtek.sjtekcontrol.settings.User;
import nl.sjtek.sjtekcontrol.utils.Executor;
import nl.sjtek.sjtekcontrol.utils.lastfm.Album;
import nl.sjtek.sjtekcontrol.utils.lastfm.Artist;
import nl.sjtek.sjtekcontrol.utils.lastfm.LastFM;
import org.bff.javampd.file.MPDFile;
import org.bff.javampd.player.Player;
import org.bff.javampd.server.MPD;
import org.bff.javampd.server.MPDConnectionException;
import org.bff.javampd.song.MPDSong;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;


@SuppressWarnings({"UnusedParameters", "unused"})
public class Music extends BaseModule {

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
    }

    /**
     * Toggle player from PLAY to PAUSE or PAUSE to PLAY or STOPPED to PLAY.
     *
     * @param arguments Arguments
     */
    public void toggle(Arguments arguments) {
        Player player = mpd.getPlayer();
        Player.Status status = player.getStatus();
        if (status == Player.Status.STATUS_STOPPED) {
            player.play();
        } else {
            player.pause();
        }
    }

    /**
     * Toggle player to PLAY.<br>
     * If an URL is specified it will insert this after the current playing song and start it.
     *
     * @param arguments Uses URL
     */
    public void play(Arguments arguments) {
        int length = mpd.getPlaylist().getSongList().size();

        if (length == 0) {
            start(new Arguments());
        } else {
            mpd.getPlayer().play();
        }
    }

    /**
     * Toggle player to PAUSE if it is PLAY.
     *
     * @param arguments Arguments
     */
    public void pause(Arguments arguments) {
        Player player = mpd.getPlayer();
        Player.Status status = player.getStatus();
        if (status == Player.Status.STATUS_PLAYING) {
            player.pause();
        }
    }

    /**
     * Toggle player to STOP.
     *
     * @param arguments Arguments
     */
    public void stop(Arguments arguments) {
        if (mpd.getPlayer().getStatus() != Player.Status.STATUS_STOPPED) {
            mpd.getPlayer().stop();
        } else {
            clear(new Arguments());
        }
    }

    /**
     * Go to the next song in the queue.
     *
     * @param arguments Arguments
     */
    public void next(Arguments arguments) {
        if (arguments.getUrl() == null) {
            mpd.getPlayer().playNext();
        } else {
            try {
                String command =
                        "/usr/bin/mpc"
                                + " -h "
                                + SettingsManager.getInstance().getMusic().getMpdHost()
                                + " insert "
                                + arguments.getUrl();
                Executor.execute(command);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                mpd.getPlayer().playNext();
            }
        }
    }

    /**
     * Go to the previous song in the queue.
     *
     * @param arguments Arguments
     */
    public void previous(Arguments arguments) {
        mpd.getPlayer().playPrevious();
    }

    /**
     * Shuffle the queue. This will not stop playback.
     *
     * @param arguments Arguments
     */
    public void shuffle(Arguments arguments) {
        mpd.getPlaylist().shuffle();
    }

    /**
     * Clear the queue. This wil set the player to STOP.
     *
     * @param arguments Arguments
     */
    public void clear(Arguments arguments) {
        mpd.getPlaylist().clearPlaylist();
    }

    /**
     * Empty.
     *
     * @param arguments Arguments
     */
    public void current(Arguments arguments) {

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
        if (arguments.getUrl() != null && !arguments.getUrl().isEmpty()) {
            path = arguments.getUrl();
            injectTaylorSwift = false;
        } else {
            User user = arguments.getUser();
            path = user.getDefaultPlaylist();
            injectTaylorSwift = user.isInjectTaylorSwift();
        }

        MPDFile mpdFile = new MPDFile();
        mpdFile.setPath(path);
        mpd.getPlaylist().addFileOrDirectory(mpdFile);

        if (injectTaylorSwift) {
            MPDFile tt = new MPDFile();
            tt.setPath(SettingsManager.getInstance().getMusic().getTaylorSwiftPath());
            mpd.getPlaylist().addFileOrDirectory(tt);
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
        int newVolume = mpd.getPlayer().getVolume() - SettingsManager.getInstance().getMusic().getVolumeStepDown();
        if (newVolume < 0) newVolume = 0;
        mpd.getPlayer().setVolume(newVolume);
    }

    /**
     * Raise the volume.
     *
     * @param arguments Arguments
     */
    public void volumeraise(Arguments arguments) {
        int newVolume = mpd.getPlayer().getVolume() + SettingsManager.getInstance().getMusic().getVolumeStepUp();
        if (newVolume > 100) newVolume = 100;
        mpd.getPlayer().setVolume(newVolume);
    }

    /**
     * Set the volume.
     *
     * @param arguments Arguments
     */
    public void volumeneutral(Arguments arguments) {
        mpd.getPlayer().setVolume(SettingsManager.getInstance().getMusic().getVolumeNeutral());
    }

    public boolean isPlaying() {
        return mpd.getPlayer().getStatus() == Player.Status.STATUS_PLAYING;
    }

    private void updateMusicState() {
        Player player = null;
        MPDSong song;
        Player.Status status = null;
        try {
            player = mpd.getPlayer();
            song = player.getCurrentSong();
            status = player.getStatus();
            musicState.setVolume(player.getVolume());
            if (status != Player.Status.STATUS_PLAYING && status != Player.Status.STATUS_PAUSED) {
                song = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            song = null;
        }
        if (song != null) {
            musicState.setAlbumAndArtist(song.getAlbumName(), song.getArtistName());
            musicState.setTitle(song.getName());
            musicState.setTimeElapsed(player.getElapsedTime());
            musicState.setTimeTotal(player.getTotalTime());
            musicState.setStatus(status);
        } else {
            musicState.setAlbumAndArtist("", "");
            musicState.setTitle("");
            musicState.setTimeElapsed(0);
            musicState.setTimeTotal(0);
            musicState.setStatus(status);
        }
    }

    @Override
    public JSONObject toJson() {
        updateMusicState();
        return musicState.toJson();
    }

    @Override
    public String getSummaryText() {
        Player.Status status = mpd.getPlayer().getStatus();
        switch (status) {
            case STATUS_STOPPED:
                return "The music is stopped.";
            case STATUS_PLAYING:
                return "The current playing song is " + musicState.title + " by " + musicState.artist + ".";
            case STATUS_PAUSED:
                return "The music is paused.";
            default:
                return "Unknown status";
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
        private Album lastFMAlbum = null;
        private Artist lastFMArtist = null;

        public void setTitle(String title) {
            this.title = title;
        }

        public void setAlbumAndArtist(String album, String artist) {

            if (!this.album.equals(album) || !this.artist.equals(artist)) {
                String artists[] = artist.split(";");
                if (!this.artist.equals(artist)) {
                    lastFMArtist = LastFM.getInstance().getArtist(artists[0]);
                }

                if (!this.album.equals(album)) {
                    lastFMAlbum = LastFM.getInstance().getAlbum(artists[0], album);
                }
            }

            this.album = album;
            this.artist = artist;
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

        public JSONObject toJson() {
            JSONObject jsonSong = new JSONObject();
            jsonSong.put("artist", artist);
            jsonSong.put("title", title);
            jsonSong.put("album", album);
            jsonSong.put("total", timeTotal);
            jsonSong.put("elapsed", timeElapsed);
//            jsonSong.put("albumArt", "moet jij geen stage zoeken?");
//            jsonSong.put("artistArt", "moet jij geen stage zoeken?");
            if (lastFMAlbum != null && lastFMAlbum.isValid()) {
                jsonSong.put("albumArt", lastFMAlbum.getImage().getMega());
            } else {
                jsonSong.put("albumArt", "");
            }

            if (lastFMArtist != null && lastFMArtist.isValid()) {
                jsonSong.put("artistArt", lastFMArtist.getImage().getMega());
            } else {
                jsonSong.put("artistArt", "");
            }

            JSONObject jsonMusic = new JSONObject();
            jsonMusic.put("song", jsonSong);
            jsonMusic.put("volume", volume);
            jsonMusic.put("state", status);

            return jsonMusic;
        }
    }
}