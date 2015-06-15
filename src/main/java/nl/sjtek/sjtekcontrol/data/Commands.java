package nl.sjtek.sjtekcontrol.data;

public final class Commands {

    private Commands() {
    }

    public static final String BASE = "http://192.168.0.64/cgi-bin/";

    public static final String SWITCH = BASE + "switch";
    public static final String INFO = BASE + "info";

    public static final class lights {
        public static final class c1 {
            public static final String ON = BASE + "lights/1/on";
            public static final String OFF = BASE + "lights/1/off";
            public static final String TOGGLE = BASE + "lights/1/toggle";
        }

        public static final class c2 {
            public static final String ON = BASE + "lights/2/on";
            public static final String OFF = BASE + "lights/2/off";
            public static final String TOGGLE = BASE + "lights/2/toggle";
        }

        public static final class c3 {
            public static final String ON = BASE + "lights/3/on";
            public static final String OFF = BASE + "lights/3/off";
            public static final String TOGGLE = BASE + "lights/3/toggle";
        }
    }

    public static final class music {
        public static final String TOGGLE = BASE + "music/toggle";
        public static final String PLAY = BASE + "music/play";
        public static final String PAUSE = BASE + "music/pause";
        public static final String STOP = BASE + "music/stop";

        public static final String NEXT = BASE + "music/next";
        public static final String PREVIOUS = BASE + "music/previous";
        public static final String REWIND = BASE + "music/rewind";

        public static final String SHUFFLE = BASE + "music/shuffle";
        public static final String CLEAR = BASE + "music/clear";

        public static final String CURRENT = BASE + "music/current";
        public static final String INFO = BASE + "music/info";

        public static String start(int id) {
            switch (id) {
                case 0:
                    return BASE + "music/start/sjteksjpeellijst";
                default:
                    return "";
            }
        }
    }

    public static final class tv {
        public static final String OFF = BASE + "tv/off";
        public static final String ENTER = BASE + "tv/enter";
        public static final String BACK = BASE + "tv/back";
        public static final String HOME = BASE + "tv/home";
        public static final String INPUT = BASE + "tv/input";
    }

    public static final class volume {
        public static final class line {
            public static final String LOWER = BASE + "volume/line/-";
            public static final String RAISE = BASE + "volume/line/+";
        }

        public static final class music {
            public static final String LOWER = BASE + "volume/music/-";
            public static final String RAISE = BASE + "volume/music/+";
            public static final String CURRENT = BASE + "volume/music/current";
            public static final String NORMAL = BASE + "volume/music/normal";
        }

        public static final class tv {
            public static final String LOWER = BASE + "volume/tv/-";
            public static final String RAISE = BASE + "volume/tv/+";
        }
    }
}
