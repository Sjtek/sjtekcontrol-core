package nl.sjtek.control.data.actions;

/**
 * Custom API action.
 */
public class CustomAction implements ActionInterface {
    private final String action;

    public CustomAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }

    @Override
    public String getUrl() {
        return action;
    }

    @Override
    public String getPath() {
        return action;
    }
}
