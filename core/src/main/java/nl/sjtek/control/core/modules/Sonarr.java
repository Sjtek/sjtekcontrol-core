package nl.sjtek.control.core.modules;

import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.responses.SonarrResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by wouter on 20-10-15.
 */
public class Sonarr extends BaseModule {

    private static final String BASE_URL = "https://sjtek.nl/sonarr/api";
    private static final String URL_CALENDAR = BASE_URL + "/calendar";
    private static final String URL_DISKSPACE = BASE_URL + "/diskspace";
    private static final String API_KEY = "4259f1a8e0cb4f6098c3560b20320d68";
    private static final int INTERVAL = 3600000;

    private List<SonarrResponse.Episode> upcoming = new ArrayList<>();
    private Map<String, SonarrResponse.Disk> disks = new HashMap<>();

    public Sonarr() {
        new Timer().scheduleAtFixedRate(new UpdateTask(), 0, INTERVAL);
    }

    private void parseCalendar(String jsonString) {
        upcoming = new ArrayList<>();
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
                this.upcoming.add(new SonarrResponse.Episode(seriesTitle, episodeName, airDate, airDateUTC, seasonInt, episodeInt));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            this.upcoming = new ArrayList<>();
        }
    }

    private void parseDiskSpace(String jsonString) {
        disks.clear();
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("path");
            if (name.equals("/") || name.equals("/tv")) {
                SonarrResponse.Disk disk = new SonarrResponse.Disk(jsonObject.getDouble("freeSpace"), jsonObject.getDouble("totalSpace"));
                disks.put(name, disk);
            }
        }
    }

    @Override
    public Response getResponse() {
        return new SonarrResponse(upcoming, disks);
    }

    @Override
    public String getSummaryText() {
        if (upcoming.size() == 0) {
            return "There are no upcoming episodes.";
        } else {
            SonarrResponse.Episode episode = upcoming.get(0);
            return "The next upcoming episode is " +
                    episode.getEpisodeName() +
                    " from " + episode.getSeriesTitle() +
                    " on " + episode.getAirDate() + ".";
        }
    }

    private void update() {
        parseCalendar(download(URL_CALENDAR));
        parseDiskSpace(download(URL_DISKSPACE));
    }

    private synchronized String download(String urlString) {
        int responseCode = -1;
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Api-Key", API_KEY);
            connection.connect();
            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedInputStream bufIn =
                        new BufferedInputStream(connection.getInputStream());
                byte[] buffer = new byte[1024];
                int n;
                ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
                while ((n = bufIn.read(buffer)) > 0) {
                    bufOut.write(buffer, 0, n);
                }

                System.out.println("" + responseCode + " - downloaded " + urlString);
                return new String(bufOut.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("" + responseCode + " - downloaded " + urlString);
        return "";
    }

    private class UpdateTask extends TimerTask {

        @Override
        public void run() {
            update();
        }
    }
}