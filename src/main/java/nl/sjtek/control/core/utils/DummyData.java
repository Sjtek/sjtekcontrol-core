package nl.sjtek.control.core.utils;

import nl.sjtek.control.core.settings.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wouter on 10-1-16.
 */
public class DummyData {

    public static SettingsManager getSettingsManager() {
        return new SettingsManager(
                getDefaultMusic(),
                getDefaultTV(),
                getDefaultQuotes(),
                getDefaultLastFM(),
                getDefaultUsers()
        );
    }

    private static Music getDefaultMusic() {
        return new Music(
                "mopidy",
                6680,
                "Local media/Taylor Swift",
                10, 3, 3
        );
    }

    private static Quotes getDefaultQuotes() {
        return new Quotes(new String[]{
                "Alleen massaproductie",
                "Dien mam",
                "Mwoah, Gertje",
                "Analytsamson",
                "Een frietkraam dat geen frieten verkoopt",
                "Moet hebben, afblijven",
                "Sjtek meester ras",
                "Ik heb je liefdestruffel naar binnen geramt",
                "Ja joa",
                "10/10 would yolo again"
        });
    }

    private static TV getDefaultTV() {
        return new TV(
                "192.168.0.66",
                8080,
                "861540"
        );
    }

    private static LastFM getDefaultLastFM() {
        return new LastFM(
                "",
                "/var/sjtekcontrol/lastfm/artists.json",
                "/var/sjtekcontrol/lastfm/albums.json"
        );
    }

    private static Map<String, User> getDefaultUsers() {
        Map<String, User> userSettings = new HashMap<>();
        Map<String, String> plSjtek = new HashMap<>();
        plSjtek.put("SjtekSjpeellijst", "spotify:user:1133212423:playlist:2A8r6F6GiLwpBCUQ0ImYKW");
        userSettings.put("default", new User(
                new String[]{
                        ""
                },
                new PlaylistSet("SjtekSjpeellijst", plSjtek),
                new String[]{
                        "13376969"
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
                false, true, false
        ));
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
                false, false, false

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
                false, false, false
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
                false, false, false
        ));
        return userSettings;
    }
}
