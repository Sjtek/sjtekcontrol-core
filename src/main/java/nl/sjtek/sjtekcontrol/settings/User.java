package nl.sjtek.sjtekcontrol.settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by wouter on 11-12-15.
 */
public class User {
    private final String[] nickNames;
    private final PlaylistSet playlistSet;
    private final String[] nfcTags;
    private final String[][] greetings;
    private final String[][] farewells;
    private final boolean checkExtraLight;

    public User(
            String[] nickNames, PlaylistSet playlistSet, String[] nfcTags,
            String[][] greetings, String[][] farewells, boolean checkExtraLight) {
        this.nickNames = nickNames;
        this.playlistSet = playlistSet;
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
        Map<String, String> plWouter = new HashMap<>();
        plWouter.put("Dinges7", "spotify:user:1133212423:playlist:75lLEqfsZ0lR1L8xz61TnB");
        plWouter.put("Dinges3", "spotify:user:1133212423:playlist:6clH0v0FfHyjsEiHgULafH");
        plWouter.put("Dinges4", "spotify:user:1133212423:playlist:6GoCxjOJ5pXgr74Za0z9bt");
        userSettings.put("wouter", new User(
                new String[]{
                        "sir wouter",
                        "lord habets"
                },
                new PlaylistSet("Dinges7", plWouter),
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
        Map<String, String> plTijn = new HashMap<>();
        plTijn.put("Swek muziek", "spotify:user:1123840057:playlist:1kbSO9MqJMWOdsIfPhjcvW");
        userSettings.put("tijn", new User(
                new String[]{
                        "3D",
                        "master renders"
                },
                new PlaylistSet("Swek muziek", plTijn),
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
        Map<String, String> plKevin = new HashMap<>();
        plKevin.put("sjpeellist", "spotify:user:1130395265:playlist:5UOGVcoR34i1XUFLYCXbnz");
        userSettings.put("kevin", new User(
                new String[]{
                        "kevin"
                },
                new PlaylistSet("sjpeellist", plKevin),
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

    public Map<String, String> getPlaylists() {
        return playlistSet.getPlaylists();
    }

    public String getDefaultPlaylist() {
        return playlistSet.getDefaultPlaylist();
    }

    public PlaylistSet getPlaylistSet() {
        return playlistSet;
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
