package nl.sjtek.control.data.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wouter on 28-1-16.
 */
public class MusicResponse extends Response {

    private final Song song;
    private final int volume;
    @SerializedName("state")
    private final String status;

    public MusicResponse(Song song, int volume, String status) {
        this.song = song;
        this.volume = volume;
        this.status = status;
    }

    public Song getSong() {
        return song;
    }

    public int getVolume() {
        return volume;
    }

    public String getStatus() {
        return status;
    }

    public static class Song {
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
