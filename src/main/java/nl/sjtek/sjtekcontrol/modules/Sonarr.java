package nl.sjtek.sjtekcontrol.modules;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wouter on 20-10-15.
 */
public class Sonarr extends BaseModule {

    private static final String BASE_URL = "https://sjtek.nl/sonarr/api/calendar";
    private static final String API_KEY = "4259f1a8e0cb4f6098c3560b20320d68";
    private static final int INTERVAL = 3600000;

    private ArrayList<Episode> episodes = new ArrayList<>();

    public Sonarr() {
        new Timer().scheduleAtFixedRate(new UpdateTask(), 0, INTERVAL);
    }

    public static void main(String args[]) {
        Sonarr sonarr = new Sonarr();
        sonarr.update();
        System.out.println(sonarr.toJson().toString());
    }

    private void parseCalendar(String jsonString) {
        episodes = new ArrayList<>();
        try {
            JSONArray jsonEpisodes = new JSONArray(jsonString);
            for (int i = 0; i < jsonEpisodes.length(); i++) {
                JSONObject jsonEpisode = jsonEpisodes.getJSONObject(i);
                JSONObject jsonSeries = jsonEpisode.getJSONObject("series");
                String seriesTitle = jsonSeries.getString("title");
                String episodeName = jsonEpisode.getString("title");
                String airDate = jsonEpisode.getString("airDate");
                String airDateUTC = jsonEpisode.getString("airDateUtc");
                int seasonInt = jsonEpisode.getInt("seasonNumber");
                int episodeInt = jsonEpisode.getInt("episodeNumber");
                this.episodes.add(new Episode(seriesTitle, episodeName, airDate, airDateUTC, seasonInt, episodeInt));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            this.episodes = new ArrayList<>();
        }
    }

    @Override
    public JSONObject toJson() {
        JSONArray jsonEpisodes = new JSONArray();

        for (Episode episode : episodes) {
            jsonEpisodes.put(episode.toJsonObject());
        }

        JSONObject jsonSonarr = new JSONObject();
        jsonSonarr.put("upcoming", jsonEpisodes);
        return jsonSonarr;
    }

    @Override
    public String getSummaryText() {
        if (episodes.size() == 0) {
            return "There are no upcoming episodes.";
        } else {
            Episode episode = episodes.get(0);
            return "The next upcoming episode is " +
                    episode.episodeName +
                    " from " + episode.seriesTitle +
                    " on " + episode.airDate + ".";
        }
    }

    private synchronized void update() {
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Api-Key", API_KEY);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedInputStream bufIn =
                        new BufferedInputStream(connection.getInputStream());
                byte[] buffer = new byte[1024];
                int n;
                ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
                while ((n = bufIn.read(buffer)) > 0) {
                    bufOut.write(buffer, 0, n);
                }

                parseCalendar(new String(bufOut.toByteArray()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            update();
        }
    }

    public class Episode {
        private final String seriesTitle, episodeName;
        private final String airDate, airDateUTC;
        private final int seasonInt, episodeInt;

        public Episode(String seriesTitle, String episodeName, String airDate, String airDateUTC, int seasonInt, int episodeInt) {
            this.seriesTitle = seriesTitle;
            this.episodeName = episodeName;
            this.airDate = airDate;
            this.airDateUTC = airDateUTC;
            this.seasonInt = seasonInt;
            this.episodeInt = episodeInt;
        }

        public JSONObject toJsonObject() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("seriesTitle", seriesTitle);
            jsonObject.put("episodeName", episodeName);
            jsonObject.put("airDate", airDate);
            jsonObject.put("airDateUTC", airDateUTC);
            jsonObject.put("seasonNumber", seasonInt);
            jsonObject.put("episodeInt", episodeInt);
            return jsonObject;
        }

        @Override
        public String toString() {
            return toJsonObject().toString();
        }
    }
}
