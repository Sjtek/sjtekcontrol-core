package nl.sjtek.sjtekcontrol.utils.lastfm;

/**
 * Created by wouter on 21-12-15.
 */
public class Album extends CacheItem {
    private final String artist;
    private final String name;
    private final Image image;

    public Album() {
        this.artist = this.name = "";
        this.image = new Image();
    }

    public Album(String artist, String name, Image image) {
        this.artist = artist;
        this.name = name;
        this.image = image;
    }

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public boolean isValid() {
        return !(artist.isEmpty() || name.isEmpty());
    }

    @Override
    public String toString() {
        return artist + " - " + name;
    }
}
