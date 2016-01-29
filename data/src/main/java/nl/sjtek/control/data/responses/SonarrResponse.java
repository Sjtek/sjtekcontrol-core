package nl.sjtek.control.data.responses;

import java.util.List;
import java.util.Map;

/**
 * Created by wouter on 28-1-16.
 */
public class SonarrResponse extends Response {

    private final List<Episode> upcoming;
    private final Map<String, Disk> diskUsage;

    public SonarrResponse(List<Episode> upcoming, Map<String, Disk> diskUsage) {
        this.upcoming = upcoming;
        this.diskUsage = diskUsage;
    }

    public List<Episode> getUpcoming() {
        return upcoming;
    }

    public Map<String, Disk> getDiskUsage() {
        return diskUsage;
    }

    public static class Episode {
        private final String seriesTitle;
        private final String episodeName;
        private final String airDate;
        private final String airDateUTC;
        private final int seasonInt;
        private final int episodeInt;

        public Episode(String seriesTitle, String episodeName, String airDate, String airDateUTC, int seasonInt, int episodeInt) {
            this.seriesTitle = seriesTitle;
            this.episodeName = episodeName;
            this.airDate = airDate;
            this.airDateUTC = airDateUTC;
            this.seasonInt = seasonInt;
            this.episodeInt = episodeInt;
        }

        public String getSeriesTitle() {
            return seriesTitle;
        }

        public String getEpisodeName() {
            return episodeName;
        }

        public String getAirDate() {
            return airDate;
        }

        public String getAirDateUTC() {
            return airDateUTC;
        }

        public int getSeasonInt() {
            return seasonInt;
        }

        public int getEpisodeInt() {
            return episodeInt;
        }
    }

    public static class Disk {
        private final double free;
        private final double total;

        public Disk(double free, double total) {
            this.free = free;
            this.total = total;
        }

        public double getFree() {
            return free;
        }

        public double getTotal() {
            return total;
        }
    }
}
