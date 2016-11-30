package nl.sjtek.control.data.ampq.events;

import java.io.Serializable;

/**
 * Created by wouter on 30-11-16.
 */
public class ActionEvent implements Serializable {

    private final Type type;

    public ActionEvent(Type type) {
        this.type = type;
    }

    public enum Type {
        TOGGLE,
        START_MUSIC
    }
}
