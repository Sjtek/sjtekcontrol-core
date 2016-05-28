package nl.sjtek.control.data.settings;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wouter on 29-1-16.
 */
public class DataCollection {

    private final Map<String, PlaylistSet> playlists;
    private final Map<String, User> users;
    private final String quotes[];

    public DataCollection(Map<String, User> users, String quotes[]) {
        this.users = users;
        this.playlists = new HashMap<>();
        for (Map.Entry<String, User> entry : users.entrySet()) {
            this.playlists.put(entry.getKey(), entry.getValue().getPlaylistSet());
        }

        this.quotes = quotes;
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

    public String[] getQuotes() {
        return quotes;
    }
}
