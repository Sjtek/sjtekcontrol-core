package nl.sjtek.control.data.ampq.events;

/**
 * Event for lights.
 */
public class LightEvent {

    private static final String MESSAGE_TEMPLATE_RGB = "%02d;%01d;%03d;%03d;%03d";
    private static final String MESSAGE_TEMPLATE_STATE = "%02d;%01d";

    private final int id;
    private final boolean state;
    private final int r;
    private final int g;
    private final int b;
    private final boolean useRgb;

    /**
     * Create event for a simple light.
     *
     * @param id    Id
     * @param state State
     */
    public LightEvent(int id, boolean state) {
        this.id = id;
        this.state = state;
        this.r = this.g = this.b = 0;
        this.useRgb = false;
    }

    /**
     * Create event for a RGB light.
     *
     * @param id Id
     * @param r  Red
     * @param g  Green
     * @param b  Blue
     */
    public LightEvent(int id, int r, int g, int b) {
        this.id = id;
        this.r = r;
        this.g = g;
        this.b = b;
        this.state = !(r == 0 && g == 0 && b == 0);
        this.useRgb = true;
    }

    /**
     * Parse the light from a message.
     *
     * @param message String message
     */
    public LightEvent(String message) {
        String[] args = message.split(";");

        if (args.length >= 2) {
            this.id = Integer.parseInt(args[0]);
            this.state = (Integer.parseInt(args[1]) == 1);
            if (args.length == 5) {
                this.r = Integer.parseInt(args[2]);
                this.g = Integer.parseInt(args[3]);
                this.b = Integer.parseInt(args[4]);
                this.useRgb = !(r == 0 && g == 0 && b == 0);
            } else if (args.length == 2) {
                this.r = this.g = this.b = 0;
                this.useRgb = false;
            } else {
                throw new IllegalArgumentException("Invalid arguments count");
            }
        } else {
            throw new IllegalArgumentException("Invalid arguments count");
        }

    }

    public int getId() {
        return id;
    }

    public boolean isEnabled() {
        return state;
    }

    public int getR() {
        return r;
    }

    public int getG() {
        return g;
    }

    public int getB() {
        return b;
    }

    public boolean useRgb() {
        return useRgb;
    }

    @Override
    public String toString() {
        if (useRgb) {
            return String.format(MESSAGE_TEMPLATE_RGB, id, (state ? 1 : 0), r, g, b);
        } else {
            return String.format(MESSAGE_TEMPLATE_STATE, id, (state ? 1 : 0));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LightEvent that = (LightEvent) o;

        if (id != that.id) return false;
        if (state != that.state) return false;
        if (r != that.r) return false;
        if (g != that.g) return false;
        if (b != that.b) return false;
        return useRgb == that.useRgb;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (state ? 1 : 0);
        result = 31 * result + r;
        result = 31 * result + g;
        result = 31 * result + b;
        result = 31 * result + (useRgb ? 1 : 0);
        return result;
    }
}
