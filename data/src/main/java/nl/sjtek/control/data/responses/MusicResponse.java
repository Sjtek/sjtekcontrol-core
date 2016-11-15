package nl.sjtek.control.data.responses;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by wouter on 28-1-16.
 */
public class MusicResponse extends Response {

    private final Song song;
    private final int volume;
    @SerializedName("state")
    private final State state;

    public MusicResponse(Song song, int volume, String status) {
        type = this.getClass().getCanonicalName();
        this.song = song;
        this.volume = volume;
        this.state = State.valueOf(status);
    }

    public Song getSong() {
        return song;
    }

    public int getVolume() {
        return volume;
    }

    public State getState() {
        return state;
    }

    public enum State {
        ERROR,
        STATUS_PLAYING,
        STATUS_STOPPED,
        STATUS_PAUSED
    }

    public static class Song implements Serializable {
        private final String artist;
        private final String title;
        private final String album;
        @SerializedName("total")
        private final long timeTotal;
        @SerializedName("elapsed")
        private final long timeElapsed;
        private final String albumArt;
        private final String artistArt;

        public Song(String artist, String title, String album, long timeTotal, long timeElapsed, String albumArt, String artistArt) {
            this.artist = artist;
            this.title = title;
            this.album = album;
            this.timeTotal = timeTotal;
            this.timeElapsed = timeElapsed;
            this.albumArt = albumArt;
            this.artistArt = artistArt;
        }

        public String getArtist() {
            return artist;
        }

        public String getTitle() {
            return title;
        }

        public String getAlbum() {
            return album;
        }

        public long getTimeTotal() {
            return timeTotal;
        }

        public long getTimeElapsed() {
            return timeElapsed;
        }

        public String getAlbumArt() {
            return albumArt;
        }

        public String getArtistArt() {
            return artistArt;
        }
    }
}
