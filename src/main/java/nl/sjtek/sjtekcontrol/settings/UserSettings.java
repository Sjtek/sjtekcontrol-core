package nl.sjtek.sjtekcontrol.settings;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wouter on 11-12-15.
 */
public class UserSettings {
    private final String[] nickNames;
    private final String[] playlists;
    private final String[] nfcTags;
    private final String[][] greetings;
    private final String[][] farewells;

    public UserSettings(
            String[] nickNames, String[] playlists, String[] nfcTags,
            String[][] greetings, String[][] farewells) {
        this.nickNames = nickNames;
        this.playlists = playlists;
        this.nfcTags = nfcTags;
        this.greetings = greetings;
        this.farewells = farewells;
    }

    public static Map<String, UserSettings> getDefaults() {
        Map<String, UserSettings> userSettings = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        userSettings.put("Wouter", new UserSettings(
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
                }

        ));
        userSettings.put("Tijn", new UserSettings(
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
                }
        ));
        userSettings.put("Kevin", new UserSettings(
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
                }
        ));
        return userSettings;
    }

    public String[] getNickNames() {
        return nickNames;
    }

    public String[] getPlaylists() {
        return playlists;
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
}
