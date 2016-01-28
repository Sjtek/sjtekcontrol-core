package nl.sjtek.control.core.utils.lastfm;

/**
 * Created by wouter on 21-12-15.
 */
public class Artist extends CacheItem {
    private final String name;
    private final Image image;

    public Artist() {
        this.name = "";
        this.image = new Image();
    }

    public Artist(String name, Image image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public boolean isValid() {
        return !(name.isEmpty());
    }

    @Override
    public String toString() {
        return name;
    }
}
