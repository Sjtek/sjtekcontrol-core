package nl.sjtek.control.core.modules;

import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.core.settings.SettingsManager;
import nl.sjtek.control.core.utils.Executor;
import nl.sjtek.control.core.utils.lastfm.Album;
import nl.sjtek.control.core.utils.lastfm.Artist;
import nl.sjtek.control.core.utils.lastfm.LastFM;
import nl.sjtek.control.data.responses.MusicResponse;
import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.settings.User;
import org.bff.javampd.file.MPDFile;
import org.bff.javampd.player.Player;
import org.bff.javampd.server.MPD;
import org.bff.javampd.server.MPDConnectionException;
import org.bff.javampd.song.MPDSong;

import java.io.IOException;
import java.net.UnknownHostException;


@SuppressWarnings({"UnusedParameters", "unused"})
public class Music extends BaseModule {

    private MPD mpd = null;
    private MusicResponse musicResponse;

    /**
     * Connect to the default MPD server.
     *
     * @throws UnknownHostException
     * @throws MPDConnectionException
     */
    public Music(String key) throws UnknownHostException, MPDConnectionException {
        super(key);
        MPD.Builder builder = new MPD.Builder();
        builder.server(SettingsManager.getInstance().getMusic().getMpdHost());
        builder.port(SettingsManager.getInstance().getMusic().getMpdPort());
        mpd = builder.build();
    }

    /**
     * Connect to an MPD server with a hostname and port;
     *
     * @param host Hostname for the MPD server
     * @param port Port for the MPD server
     * @throws UnknownHostException
     * @throws MPDConnectionException
     */
    public Music(String key, String host, int port) throws UnknownHostException, MPDConnectionException {
        super(key);
        MPD.Builder builder = new MPD.Builder();
        builder.server(host);
        builder.port(port);
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
        if (arguments.getUrl() == null || arguments.getUrl().isEmpty()) {
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
        MusicResponseBuilder builder = new MusicResponseBuilder();
        try {
            player = mpd.getPlayer();
            song = player.getCurrentSong();
            status = player.getStatus();
            builder.setVolume(player.getVolume());
            if (status != Player.Status.STATUS_PLAYING && status != Player.Status.STATUS_PAUSED) {
                song = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            song = null;
        }
        if (song != null) {
            builder.setArtistAndAlbum(song.getArtistName(), song.getAlbumName());
            builder.setTitle(song.getName());
            builder.setTimeElapsed(player.getElapsedTime());
            builder.setTimeTotal(player.getTotalTime());
            builder.setStatus(status);
        } else {
            builder.setArtistAndAlbum("", "");
            builder.setTitle("");
            builder.setTimeElapsed(0);
            builder.setTimeTotal(0);
            builder.setStatus(status);
        }
        musicResponse = builder.build();
    }

    @Override
    public Response getResponse() {
        updateMusicState();
        return musicResponse;
    }

    @Override
    public String getSummaryText() {
        Player.Status status = mpd.getPlayer().getStatus();
        switch (status) {
            case STATUS_STOPPED:
                return "The music is stopped.";
            case STATUS_PLAYING:
                return "The current playing song is " + musicResponse.getSong().getTitle() + " by " + musicResponse.getSong().getArtist() + ".";
            case STATUS_PAUSED:
                return "The music is paused.";
            default:
                return "Unknown status";
        }
    }

    private class MusicResponseBuilder {
        private String artist;
        private String title;
        private String album;
        private long timeTotal;
        private long timeElapsed;
        private int volume;
        private String status;
        private String albumArt = "";
        private String artistArt = "";

        public MusicResponseBuilder() {

        }

        public MusicResponseBuilder setArtistAndAlbum(String artist, String album) {

            String previousArtist = "";
            String previousAlbum = "";
            String artistArt = "";
            String albumArt = "";
            if (musicResponse != null) {
                previousArtist = musicResponse.getSong().getArtist();
                previousAlbum = musicResponse.getSong().getAlbum();
                artistArt = musicResponse.getSong().getArtistArt();
                albumArt = musicResponse.getSong().getAlbumArt();
            }

            if (!previousAlbum.equals(album) || !previousArtist.equals(artist)) {
                String artists[] = artist.split(";");
                if (!previousArtist.equals(artist)) {
                    Artist lastFMArtist = LastFM.getInstance().getArtist(artists[0]);
                    if (lastFMArtist != null) {
                        artistArt = lastFMArtist.getImage().getMega();
                    } else {
                        artistArt = "";
                    }
                }

                if (!previousAlbum.equals(album)) {
                    Album lastFMAlbum = LastFM.getInstance().getAlbum(artists[0], album);
                    if (lastFMAlbum != null) {
                        albumArt = lastFMAlbum.getImage().getMega();
                    } else {
                        albumArt = "";
                    }
                }
            }

            this.artist = artist;
            this.album = album;
            this.artistArt = artistArt;
            this.albumArt = albumArt;
            return this;
        }

        public MusicResponseBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public MusicResponseBuilder setTimeTotal(long timeTotal) {
            this.timeTotal = timeTotal;
            return this;
        }

        public MusicResponseBuilder setTimeElapsed(long timeElapsed) {
            this.timeElapsed = timeElapsed;
            return this;
        }

        public MusicResponseBuilder setVolume(int volume) {
            this.volume = volume;
            return this;
        }

        public MusicResponseBuilder setStatus(Player.Status status) {
            this.status = status != null ? status.toString() : "ERROR";
            return this;
        }

        public MusicResponse build() {
            return new MusicResponse(
                    new MusicResponse.Song(artist, title, album, timeTotal, timeElapsed, albumArt, artistArt),
                    volume, status
            );
        }
    }
}