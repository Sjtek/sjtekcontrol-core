package nl.sjtek.sjtekcontrol.devices;

import nl.sjtek.sjtekcontrol.data.Arguments;

/**
 * Created by wouter on 24-10-15.
 */
@SuppressWarnings("unused")
public class Minecraft {

    private Arguments.MinecraftData minecraftData = new Arguments.MinecraftData();

    public void set(Arguments arguments) {
        Arguments.MinecraftData minecraftData = arguments.getMinecraftData();
        if (minecraftData.isValid()) {
            this.minecraftData = minecraftData;
        } else {
            this.minecraftData = new Arguments.MinecraftData();
        }
    }

    @Override
    public String toString() {
        return minecraftData.toString();
    }
}
