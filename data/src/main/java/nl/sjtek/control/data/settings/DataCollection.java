package nl.sjtek.control.data.settings;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wouter on 29-1-16.
 */
public class DataCollection {

    private final Map<String, PlaylistSet> playlists;
    private final Map<String, User> users;

    public DataCollection(Map<String, PlaylistSet> playlists, Map<String, User> users) {
        this.playlists = playlists;
        this.users = users;
    }

    public DataCollection(Map<String, User> users) {
        this.users = users;
        this.playlists = new HashMap<>();
        for (Map.Entry<String, User> entry : users.entrySet()) {
            this.playlists.put(entry.getKey(), entry.getValue().getPlaylistSet());
        }
    }

    public static DataCollection fromJson(String data) {
        return new Gson().fromJson(data, DataCollection.class);
    }

    public Map<String, PlaylistSet> getPlaylists() {
        return playlists;
    }

    public Map<String, User> getUsers() {
        return users;
    }
}
