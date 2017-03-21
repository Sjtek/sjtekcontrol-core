package nl.sjtek.control.core.modules;

import nl.sjtek.control.core.events.AudioEvent;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.core.settings.SettingsManager;
import nl.sjtek.control.core.utils.lastfm.Album;
import nl.sjtek.control.core.utils.lastfm.Artist;
import nl.sjtek.control.core.utils.lastfm.LastFM;
import nl.sjtek.control.data.responses.MusicResponse;
import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.settings.User;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.bff.javampd.file.MPDFile;
import org.bff.javampd.player.Player;
import org.bff.javampd.server.ConnectionChangeEvent;
import org.bff.javampd.server.ConnectionChangeListener;
import org.bff.javampd.server.MPD;
import org.bff.javampd.server.MPDConnectionException;
import org.bff.javampd.song.MPDSong;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;


@SuppressWarnings({"UnusedParameters", "unused"})
public class Music extends BaseModule implements ConnectionChangeListener {

    private final String host;
    private final int port;
    private MPD mpd = null;
    private WSUpdateListener updateListener;
    private MusicResponse musicResponse;
    private boolean timerRunning;

    /**
     * Connect to the default MPD server.
     *
     * @throws UnknownHostException
     * @throws MPDConnectionException
     */
    public Music(String key) throws UnknownHostException, MPDConnectionException, URISyntaxException {
        this(key, SettingsManager.getInstance().getMusic().getMpdHost(), SettingsManager.getInstance().getMusic().getMpdPort());
    }

    /**
     * Connect to an MPD server with a hostname and port;
     *
     * @param host Hostname for the MPD server
     * @param port Port for the MPD server
     * @throws UnknownHostException
     * @throws MPDConnectionException
     */
    public Music(String key, String host, int port) throws UnknownHostException, MPDConnectionException, URISyntaxException {
        super(key);

        this.host = host;
        this.port = port;

        MPD.Builder builder = new MPD.Builder();
        builder.server(host);
        builder.port(port);
        mpd = builder.build();

        updateListener = new WSUpdateListener(host, port);
        updateListener.tryConnect();

        mpd.getMonitor().addConnectionChangeListener(this);
        mpd.getMonitor().start();
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
        dataChanged();
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
        dataChanged();
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
        dataChanged();
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
        dataChanged();
    }

    /**
     * Go to the next song in the queue.
     *
     * @param arguments Arguments
     */
    public void next(Arguments arguments) {
        mpd.getPlayer().playNext();
        dataChanged();
    }

    /**
     * Go to the previous song in the queue.
     *
     * @param arguments Arguments
     */
    public void previous(Arguments arguments) {
        mpd.getPlayer().playPrevious();
        dataChanged();
    }

    /**
     * Shuffle the queue. This will not stop playback.
     *
     * @param arguments Arguments
     */
    public void shuffle(Arguments arguments) {
        mpd.getPlaylist().shuffle();
        dataChanged();
    }

    /**
     * Clear the queue. This wil set the player to STOP.
     *
     * @param arguments Arguments
     */
    public void clear(Arguments arguments) {
        mpd.getPlaylist().clearPlaylist();
        dataChanged();
    }

    /**
     * Empty.
     *
     * @param arguments Arguments
     */
    public void current(Arguments arguments) {

    }

    public void start(Arguments arguments) {
        start(arguments, true);
    }

    /**
     * Clear queue and stop player. Then add SjtekSjpeellijst and Taylor Swift, shuffle it and start playback.
     *
     * @param arguments Arguments
     */
    public void start(Arguments arguments, boolean changeVolume) {
        Arguments dummyArguments = new Arguments();
        clear(dummyArguments);

        if (changeVolume) {
            volumeneutral(dummyArguments);
        } else {
            mpd.getPlayer().setVolume(50);
        }

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

        if (!arguments.isNoShuffle()) shuffle(dummyArguments);
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
        dataChanged();
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
        dataChanged();
    }

    /**
     * Set the volume.
     *
     * @param arguments Arguments
     */
    public void volumeneutral(Arguments arguments) {
        mpd.getPlayer().setVolume(SettingsManager.getInstance().getMusic().getVolumeNeutral());
        dataChanged();
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

        if (status != null && status == Player.Status.STATUS_PLAYING) {
            Bus.post(new AudioEvent(getKey(), true));
        } else {
            Bus.post(new AudioEvent(getKey(), false));
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

    @Override
    public boolean isEnabled(String user) {
        return musicResponse.getState() == MusicResponse.State.STATUS_PLAYING;
    }

    @Override
    public void connectionChangeEventReceived(ConnectionChangeEvent connectionChangeEvent) {
        System.out.println("Connection changed " + connectionChangeEvent.isConnected());
        dataChanged();
        if (connectionChangeEvent.isConnected()) {
            try {
                updateListener.connect();
            } catch (IllegalStateException e) {
                try {
                    updateListener = new WSUpdateListener(host, port);
                    updateListener.connect();
                } catch (URISyntaxException e1) {
                    // Should not happen
                    e1.printStackTrace();
                }
            }
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

    private class WSUpdateListener extends WebSocketClient {
        private static final String URL_TEMPLATE = "ws://%s:%d/mopidy/ws";
        private final String host;
        private final int port;
        private long lastEvent = 0;
        private Timer heartbeatTimer = new Timer();

        public WSUpdateListener(String host, int port) throws URISyntaxException {
            super(new URI(String.format(URL_TEMPLATE, host, 6680)));
            this.host = host;
            this.port = port;
        }

        public void tryConnect() {
            new PingThread().start();
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("Web socket connected");
            heartbeatTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    WSUpdateListener.this.send("{\"jsonrpc\": \"2.0\", \"id\": 1, \"method\": \"core.playback.get_state\"}");
                }
            }, 30000, 30000);
            dataChanged();
        }

        @Override
        public void onMessage(String message) {
            if (!message.contains("jsonrpc")) {
                if (!(message.equals("{\"event\": \"tracklist_changed\"}"))) {
                    if (System.currentTimeMillis() - lastEvent > 40) {
                        System.out.println((System.currentTimeMillis() - lastEvent) + " - " + message);
                        lastEvent = System.currentTimeMillis();
                        dataChanged();
                    }
                }
            }

        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println(this.getClass().getSimpleName() + " onClose: " + code + " " + reason);
            heartbeatTimer.cancel();
            dataChanged();
        }

        @Override
        public void onError(Exception ex) {
            System.out.println(this.getClass().getSimpleName() + " onError: " + ex.getMessage());
            dataChanged();
        }

        private class PingThread extends Thread {
            @Override
            public void run() {
                super.run();
                OkHttpClient okHttpClient = new OkHttpClient();

                boolean connected = false;
                while (true) {
                    okhttp3.Response response = null;
                    try {
                        Thread.sleep(1000);
                        Call call = okHttpClient.newCall(new Request.Builder().url("http://" + host + ":" + 6680 + "/mopidy").build());
                        response = call.execute();
                        if (response.isSuccessful()) {
                            WSUpdateListener.this.connect();
                            return;
                        }

                    } catch (IOException | InterruptedException e) {
                        System.out.println("Connection to Mopidy failed on" + host + " (" + e.getMessage() + ")");
                    } finally {
                        if (response != null) {
                            response.body().close();
                        }
                    }
                }
            }
        }
    }
}