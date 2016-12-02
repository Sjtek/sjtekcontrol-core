package nl.sjtek.control.data.actions;

/**
 * Enum for the API calls of SjtekControl
 */
public enum Action implements ActionInterface {
    /**
     * Get the state of the API.
     */
    REFRESH("/info"),
    /**
     * Master toggle for the house.
     */
    SWITCH("/toggle"),
    /**
     * Get the data of the API (settings etc.)
     */
    DATA("/data");

    private static final String API_BASE = "https://sjtek.nl/api";
    private final String path;

    Action(String urlAction) {
        this.path = urlAction;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getUrl() {
        return API_BASE + path;
    }

    @Override
    public String toString() {
        return getUrl();
    }

    /**
     * Calls for turning the lights.
     */
    public enum Light implements ActionInterface {
        TOGGLE_1("/toggle1"),
        TOGGLE_1_ON("/toggle1on"),
        TOGGLE_1_OFF("/toggle1off"),
        TOGGLE_2("/toggle2"),
        TOGGLE_2_ON("/toggle2on"),
        TOGGLE_2_OFF("/toggle2off"),
        TOGGLE_3("/toggle3"),
        TOGGLE_3_ON("/toggle3on"),
        TOGGLE_3_OFF("/toggle3off"),
        TOGGLE_4("/toggle4"),
        TOGGLE_4_ON("/toggle4on"),
        TOGGLE_4_OFF("/toggle4off");


        private static final String BASE = "/lights";
        private final String path;

        Light(String urlAction) {
            this.path = urlAction;
        }

        @Override
        public String getPath() {
            return BASE + path;
        }

        @Override
        public String getUrl() {
            return API_BASE + BASE + path;
        }

        @Override
        public String toString() {
            return getUrl();
        }
    }

    /**
     * Calls for controlling the music.
     */
    public enum Music implements ActionInterface {
        TOGGLE("/toggle"),
        PLAY("/play"),
        PAUSE("/pause"),
        STOP("/stop"),
        NEXT("/next"),
        PREVIOUS("/previous"),
        SHUFFLE("/shuffle"),
        CLEAR("/clear"),
        VOLUME_LOWER("/volumelower"),
        VOLUME_RAISE("/volumeraise"),
        VOLUME_NEUTRAL("/volumeneutral"),
        INFO("/info?voice"),
        START("/start");

        private static final String BASE = "/music";
        private final String path;

        Music(String urlAction) {
            this.path = urlAction;
        }

        @Override
        public String getPath() {
            return BASE + path;
        }

        @Override
        public String getUrl() {
            return API_BASE + BASE + path;
        }

        @Override
        public String toString() {
            return getUrl();
        }
    }

    /**
     * Calls for controlling the TV.
     */
    public enum TV implements ActionInterface {
        POWER_OFF("/off"),
        VOLUME_LOWER("/volumelower"),
        VOLUME_RAISE("/volumeraise");


        private static final String BASE = "/tv";
        private final String path;

        TV(String urlAction) {
            this.path = urlAction;
        }

        @Override
        public String getPath() {
            return BASE + path;
        }

        @Override
        public String getUrl() {
            return API_BASE + BASE + path;
        }

        @Override
        public String toString() {
            return getUrl();
        }
    }

    /**
     * Calls for controlling the coffee machine.
     */
    public enum Coffee implements ActionInterface {
        START("/start");

        private static final String BASE = "/coffee";
        private final String path;

        Coffee(String urlAction) {
            this.path = urlAction;
        }

        @Override
        public String getPath() {
            return BASE + path;
        }

        @Override
        public String getUrl() {
            return API_BASE + BASE + path;
        }

        @Override
        public String toString() {
            return getUrl();
        }
    }

    /**
     * Calls for controlling the night mode.
     */
    public enum NightMode implements ActionInterface {
        ENABLE("/enable"),
        DISABLE("/disable");

        private static final String BASE = "/nightmode";
        private final String path;

        NightMode(String urlAction) {
            this.path = urlAction;
        }

        @Override
        public String getPath() {
            return BASE + path;
        }

        @Override
        public String getUrl() {
            return API_BASE + BASE + path;
        }

        @Override
        public String toString() {
            return getUrl();
        }
    }

    /**
     * Calls for controlling the event screen.
     */
    public enum Screen implements ActionInterface {
        MODE_FULLSCREEN("/fullscreen"),
        MODE_MUSIC("/music"),
        MODE_TV("/tv"),
        MODE_COUNTDOWN("/countdown"),
        MODE_NEW_YEAR("/newyear"),
        REFRESH("/refresh");

        private static final String BASE = "/screen";
        private final String path;

        Screen(String urlAction) {
            this.path = urlAction;
        }

        @Override
        public String getPath() {
            return BASE + path;
        }

        @Override
        public String getUrl() {
            return API_BASE + BASE + path;
        }

        @Override
        public String toString() {
            return getUrl();
        }
    }
}
