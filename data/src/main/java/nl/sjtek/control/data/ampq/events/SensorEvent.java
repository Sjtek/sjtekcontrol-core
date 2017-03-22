package nl.sjtek.control.data.ampq.events;

import java.util.Locale;

/**
 * Created by wouter on 22-3-17.
 */
public class SensorEvent {

    private static final String MESSAGE_TEMPLATE = "%d;%d;%.2f";
    private final Type type;
    private final int id;
    private final float value;

    public SensorEvent(Type type, int id, float value) {
        this.type = type;
        this.id = id;
        this.value = value;
    }

    public static SensorEvent parseMessage(String message) {
        try {
            String[] args = message.split(";");
            if (args.length == 3) {
                int typeId = Integer.parseInt(args[0]);
                int id = Integer.parseInt(args[1]);
                float value = Float.parseFloat(args[2]);

                Type type = Type.ordinals[typeId];
                if (type != null) {
                    return new SensorEvent(type, id, value);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public Type getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public float getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SensorEvent that = (SensorEvent) o;

        if (id != that.id) return false;
        if (Float.compare(that.value, value) != 0) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (value != +0.0f ? Float.floatToIntBits(value) : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, MESSAGE_TEMPLATE, type.ordinal(), id, value);
    }

    public enum Type {
        MOTION,
        LIGHT,
        TEMPERATURE;

        public static final Type[] ordinals = values();
    }
}
