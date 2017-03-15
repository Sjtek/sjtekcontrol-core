package nl.sjtek.control.data.ampq.events;

/**
 * Created by wouter on 15-3-17.
 */
public class LightStateEvent {
    private static final String MESSAGE_TEMPLATE = "99;%02d;%01d";

    private final int id;
    private final boolean state;

    public LightStateEvent(int id, boolean state) {
        this.id = id;
        this.state = state;
    }

    public static LightStateEvent parseMessage(String message) {
        try {
            String[] args = message.split(";");

            if (args.length == 3) {
                if (args[0].equals("99")) {
                    int id = Integer.parseInt(args[1]);
                    boolean state = args[2].equals("1");
                    return new LightStateEvent(id, state);
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

    public int getId() {
        return id;
    }

    public boolean isEnabled() {
        return state;
    }

    @Override
    public String toString() {
        return String.format(MESSAGE_TEMPLATE, id, (state ? 1 : 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LightStateEvent that = (LightStateEvent) o;

        if (id != that.id) return false;
        return state == that.state;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (state ? 1 : 0);
        return result;
    }
}
