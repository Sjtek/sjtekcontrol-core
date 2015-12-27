package nl.sjtek.sjtekcontrol.utils.lastfm;

import nl.sjtek.sjtekcontrol.settings.SettingsManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by wouter on 21-12-15.
 */
public class LastFM {

    private static final String TAG = LastFM.class.getCanonicalName();

    private static final String URL_ARTIST = "http://ws.audioscrobbler.com/2.0/?" +
            "format=json&" +
            "method=artist.getInfo&" +
            "artist=%s&" +
            "api_key=%s";
    private static final String URL_ALBUM = "http://ws.audioscrobbler.com/2.0/?" +
            "format=json&" +
            "method=album.getInfo&" +
            "artist=%s&" +
            "album=%s&" +
            "api_key=%s";

    private static LastFM instance = new LastFM();

    private Cache cache;

    private LastFM() {
        this.cache = new Cache();
    }

    public synchronized static LastFM getInstance() {
        return instance;
    }

    public Artist getArtist(String query) {
        if (query == null || query.isEmpty()) return null;

        synchronized (TAG) {
            Artist artist = cache.getArtist(query);
            if (artist != null) {
                return artist;
            }
            System.out.println("Downloading artist " + query);
            artist = downloadArtist(query);
            cache.update(query, artist);
            System.out.println("Downloaded artist " + artist.toString());
            return artist;
        }
    }

    public Album getAlbum(String queryArtist, String queryAlbum) {
        if ((queryArtist == null || queryArtist.isEmpty()) ||
                (queryAlbum == null || queryAlbum.isEmpty())) {
            return null;
        }

        synchronized (TAG) {
            Album album = cache.getAlbum(queryArtist, queryAlbum);
            if (album != null) {
                return album;
            }

            System.out.println("Downloading album " + queryArtist + " - " + queryAlbum);
            album = downloadAlbum(queryArtist, queryAlbum);
            cache.update(queryArtist, queryAlbum, album);
            System.out.println("Downloaded album " + album.toString());
            return album;
        }
    }

    private String encodeString(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    private Album downloadAlbum(String queryArtist, String queryAlbum) {
        String url = String.format(
                URL_ALBUM,
                encodeString(queryArtist),
                encodeString(queryAlbum),
                SettingsManager.getInstance().getLastFM().getApiKey());
        String response = download(url);

        if (response != null && !response.isEmpty()) {
            try {
                JSONObject jsonAlbum = new JSONObject(response).getJSONObject("album");
                String artistName = jsonAlbum.getString("artist");
                String albumName = jsonAlbum.getString("name");
                Image image = getImage(jsonAlbum.getJSONArray("image"));
                return new Album(artistName, albumName, image);
            } catch (JSONException e) {
                e.printStackTrace();
                return new Album("", "", new Image());
            }
        } else {
            return new Album();
        }
    }

    private Artist downloadArtist(String query) {
        String response = download(String.format(
                URL_ARTIST,
                encodeString(query),
                SettingsManager.getInstance().getLastFM().getApiKey()));
        if (response != null && !response.isEmpty()) {
            try {
                JSONObject jsonArtist = new JSONObject(response).getJSONObject("artist");
                String name = jsonArtist.getString("name");
                Image image = getImage(jsonArtist.getJSONArray("image"));
                return new Artist(name, image);
            } catch (JSONException e) {
                e.printStackTrace();
                return new Artist("", new Image());
            }
        } else {
            return new Artist();
        }
    }

    private Image getImage(JSONArray jsonArray) {
        String small, medium, large, extralarge, mega, image;
        small = medium = large = extralarge = mega = image = "";

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject currentImage = jsonArray.getJSONObject(i);
            switch (currentImage.getString("size")) {
                case "small":
                    small = currentImage.getString("#text");
                    break;
                case "medium":
                    medium = currentImage.getString("#text");
                    break;
                case "large":
                    large = currentImage.getString("#text");
                    break;
                case "extralarge":
                    extralarge = currentImage.getString("#text");
                    break;
                case "mega":
                    mega = currentImage.getString("#text");
                    break;
                case "":
                    image = currentImage.getString("#text");
                    break;
            }
        }

        return new Image(small, medium, large, extralarge, mega, image);
    }


    private String download(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                StringBuilder stringBuffer = new StringBuilder();
                int character;
                while ((character = inputStream.read()) != -1) {
                    stringBuffer.append((char) character);
                }

                return new String(stringBuffer);
            } else {
                return "";
            }
        } catch (IOException e) {
            return "";
        }
    }
}
