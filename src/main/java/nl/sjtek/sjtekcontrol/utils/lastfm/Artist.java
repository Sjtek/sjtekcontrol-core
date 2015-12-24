package nl.sjtek.sjtekcontrol.utils.lastfm;

/**
 * Created by wouter on 21-12-15.
 */
public class Artist extends CacheItem {
    private final String key;
    private final String name;
    private final Image image;

    public Artist() {
        this.key = this.name = "";
        this.image = new Image();
    }

    public Artist(String key, String name, Image image) {
        this.key = key;
        this.name = name;
        this.image = image;
    }

    public static String getKey(String query) {
        return query;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    @Override
    public boolean isValid() {
        return !(key.isEmpty()) && !(name.isEmpty());
    }

    @Override
    public String toString() {
        return name;
    }
}
