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
    @SerializedName("4")
    private final boolean light4;
    @SerializedName("5")
    private final boolean light5;
    @SerializedName("6")
    private final boolean light6;
    @SerializedName("7")
    private final boolean light7;

    public LightsResponse(boolean light1, boolean light2, boolean light3, boolean light4, boolean light5, boolean light6, boolean light7) {
        this.light1 = light1;
        this.light2 = light2;
        this.light3 = light3;
        this.light4 = light4;
        this.light5 = light5;
        this.light6 = light6;
        this.light7 = light7;
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

    public boolean isLight4() {
        return light4;
    }

    public boolean isLight5() {
        return light5;
    }

    public boolean isLight6() {
        return light6;
    }

    public boolean isLight7() {
        return light7;
    }
}
