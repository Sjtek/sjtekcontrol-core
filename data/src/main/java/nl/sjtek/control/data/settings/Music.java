package nl.sjtek.control.data.settings;

/**
 * Created by wouter on 11-12-15.
 */
public class Music {
    private final String mpdHost;
    private final int mpdPort;

    private final String taylorSwiftPath;
    private final int volumeNeutral;
    private final int volumeStepUp;
    private final int volumeStepDown;

    public Music(String mpdHost, int mpdPort, String taylorSwiftPath, int volumeNeutral, int volumeStepUp, int volumeStepDown) {
        this.mpdHost = mpdHost;
        this.mpdPort = mpdPort;
        this.taylorSwiftPath = taylorSwiftPath;
        this.volumeNeutral = volumeNeutral;
        this.volumeStepUp = volumeStepUp;
        this.volumeStepDown = volumeStepDown;
    }

    public String getMpdHost() {
        return mpdHost;
    }

    public int getMpdPort() {
        return mpdPort;
    }

    public String getTaylorSwiftPath() {
        return taylorSwiftPath;
    }

    public int getVolumeNeutral() {
        return volumeNeutral;
    }

    public int getVolumeStepUp() {
        return volumeStepUp;
    }

    public int getVolumeStepDown() {
        return volumeStepDown;
    }
}
