package nl.sjtek.control.data.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wouter on 28-1-16.
 */
public class LightsResponse extends Response {

    @SerializedName("1")
    private final boolean light1;
    @SerializedName("2")
    private final boolean light2;
    @SerializedName("3")
    private final boolean light3;

    public LightsResponse(boolean light1, boolean light2, boolean light3) {
        this.light1 = light1;
        this.light2 = light2;
        this.light3 = light3;
    }

    public boolean isLight1() {
        return light1;
    }

    public boolean isLight2() {
        return light2;
    }

    public boolean isLight3() {
        return light3;
    }
}
