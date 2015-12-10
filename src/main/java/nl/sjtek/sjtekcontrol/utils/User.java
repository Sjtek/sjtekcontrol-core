package nl.sjtek.sjtekcontrol.utils;

import java.util.Random;

/**
 * Created by wouter on 28-11-15.
 */
public enum User {
    WOUTER(new String[]{
            "sir Wouter",
            "lord Habets",
    }, new String[]{
            "spotify:user:1133212423:playlist:6GoCxjOJ5pXgr74Za0z9bt",
    }),

    TIJN(new String[]{
            "3D",
            "master Renders",
    }, new String[]{
            "spotify:user:1123840057:playlist:1kbSO9MqJMWOdsIfPhjcvW",
    }),

    KEVIN(new String[]{
            "Kevin",
    }, new String[]{
            "spotify:user:1130395265:playlist:5UOGVcoR34i1XUFLYCXbnz"
    });

    static Random random = new Random();
    String[] name;
    String[] music;

    User(String[] name, String[] music) {
        this.name = name;
        this.music = music;
    }

    public String getMusic() {
        return music[0];
    }

    @Override
    public String toString() {
        return name[random.nextInt(name.length)];
    }
}
