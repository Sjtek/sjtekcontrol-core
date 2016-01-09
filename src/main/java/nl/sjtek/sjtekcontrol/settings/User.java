package nl.sjtek.sjtekcontrol.settings;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by wouter on 11-12-15.
 */
public class User {
    private final String[] nickNames;
    private final String[] playlists;
    private final String[] nfcTags;
    private final String[][] greetings;
    private final String[][] farewells;
    private final boolean checkExtraLight;

    public User(
            String[] nickNames, String[] playlists, String[] nfcTags,
            String[][] greetings, String[][] farewells, boolean checkExtraLight) {
        this.nickNames = nickNames;
        this.playlists = playlists;
        this.nfcTags = nfcTags;
        this.greetings = greetings;
        this.farewells = farewells;
        this.checkExtraLight = checkExtraLight;
    }

    public static User getUser(String name) {
        return SettingsManager.getInstance().getUser(name);
    }

    public static Map<String, User> getDefaults() {
        Map<String, User> userSettings = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        userSettings.put("wouter", new User(
                new String[]{
                        "sir wouter",
                        "lord habets"
                },
                new String[]{
                        "spotify:user:1133212423:playlist:6GoCxjOJ5pXgr74Za0z9bt"
                },
                new String[]{
                        "1853719819",       // OV Chipkaart
                        "16514020840",      // Witte kaart
                        "48115222160",      // Sleutelhanger
                },
                new String[][]{
                        new String[]{
                                "Good night %s."
                        },
                        new String[]{
                                "Good morning %s."
                        },
                        new String[]{
                                "Good afternoon %s."
                        },
                        new String[]{
                                "Good evening %s."
                        }
                },
                new String[][]{
                        new String[]{
                                "Sleep well, %s."
                        },
                        new String[]{
                                "Good luck today, %s."
                        },
                        new String[]{
                                "Good bye, %s."
                        },
                        new String[]{
                                "Have a nice evening, %s."
                        }
                },
                false

        ));
        userSettings.put("tijn", new User(
                new String[]{
                        "3D",
                        "master renders"
                },
                new String[]{
                        "spotify:user:1123840057:playlist:1kbSO9MqJMWOdsIfPhjcvW"
                },
                new String[]{
                        "552518698",        // OV Chipkaart
                        "6334480",          // Sleutelhanger
                },
                new String[][]{
                        new String[]{
                                "Good night %s."
                        },
                        new String[]{
                                "Good morning %s."
                        },
                        new String[]{
                                "Good afternoon %s."
                        },
                        new String[]{
                                "Good evening %s."
                        }
                },
                new String[][]{
                        new String[]{
                                "Sleep well, %s."
                        },
                        new String[]{
                                "Good luck today, %s."
                        },
                        new String[]{
                                "Good bye, %s."
                        },
                        new String[]{
                                "Have a nice evening, %s."
                        }
                },
                false
        ));
        userSettings.put("kevin", new User(
                new String[]{
                        "kevin"
                },
                new String[]{
                        "spotify:user:1130395265:playlist:5UOGVcoR34i1XUFLYCXbnz"
                },
                new String[]{
                        "281846010",        // Sleutelhanger
                },
                new String[][]{
                        new String[]{
                                "Good night %s."
                        },
                        new String[]{
                                "Good morning %s."
                        },
                        new String[]{
                                "Good afternoon %s."
                        },
                        new String[]{
                                "Good evening %s."
                        }
                },
                new String[][]{
                        new String[]{
                                "Sleep well, %s."
                        },
                        new String[]{
                                "Good luck today, %s."
                        },
                        new String[]{
                                "Good bye, %s."
                        },
                        new String[]{
                                "Have a nice evening, %s."
                        }
                },
                false
        ));
        return userSettings;
    }

    public String[] getNickNames() {
        return nickNames;
    }

    public String getRandomNickname() {
        return getNickNames()[new Random().nextInt(getNickNames().length)];
    }

    public String[] getPlaylists() {
        return playlists;
    }

    public String getDefaultPlaylist() {
        return getPlaylists()[0];
    }

    public String[] getNfcTags() {
        return nfcTags;
    }

    public String[][] getGreetings() {
        return greetings;
    }

    public String[][] getFarewells() {
        return farewells;
    }

    public boolean isCheckExtraLight() {
        return checkExtraLight;
    }
}
