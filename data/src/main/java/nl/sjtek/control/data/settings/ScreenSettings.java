package nl.sjtek.control.data.settings;

/**
 * Created by wouter on 26-3-16.
 */
public class ScreenSettings extends Setting {

    private final String firstTitle;
    private final String firstSubtitle;
    private final String secondTitle;

    public ScreenSettings(String firstTitle, String firstSubtitle, String secondTitle) {
        this.firstTitle = firstTitle;
        this.firstSubtitle = firstSubtitle;
        this.secondTitle = secondTitle;
    }

    public String getFirstTitle() {
        return firstTitle;
    }

    public String getFirstSubtitle() {
        return firstSubtitle;
    }

    public String getSecondTitle() {
        return secondTitle;
    }
}
