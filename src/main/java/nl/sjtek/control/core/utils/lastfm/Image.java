package nl.sjtek.control.core.utils.lastfm;

/**
 * Created by wouter on 21-12-15.
 */
public class Image {
    private final String small;
    private final String medium;
    private final String large;
    private final String extraLarge;
    private final String mega;
    private final String image;

    public Image() {
        this.small = this.medium = this.large = this.extraLarge = this.mega = this.image = "";
    }

    public Image(String small, String medium, String large, String extraLarge, String mega, String image) {
        this.small = small;
        this.medium = medium;
        this.large = large;
        this.extraLarge = extraLarge;
        this.mega = mega;
        this.image = image;
    }

    public String getSmall() {
        return small;
    }

    public String getMedium() {
        return medium;
    }

    public String getLarge() {
        return large;
    }

    public String getExtraLarge() {
        return extraLarge;
    }

    public String getMega() {
        return mega;
    }

    public String getImage() {
        return image;
    }
}
