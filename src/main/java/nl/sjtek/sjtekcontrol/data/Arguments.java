package nl.sjtek.sjtekcontrol.data;

import org.apache.commons.codec.Charsets;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.util.List;

public class Arguments {

    public enum StreamType {
        Stream,
        YouTube
    }

    private boolean useVoice = false;
    private String url = null;
    private String text = null;
    private StreamType streamType = StreamType.Stream;

    public Arguments() {

    }

    public Arguments(String query) {
        if (query == null || query.isEmpty()) {
            return;
        }

        List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(query, Charsets.UTF_8);
        for (NameValuePair nameValuePair : nameValuePairs) {
            String name = nameValuePair.getName();
            String value = nameValuePair.getValue();
            if ("voice".equals(name)) {
                useVoice = true;
            } else if ("text".equals(name)) {
                text = value;
            } else if ("url".equals(name)) {
                url = value;
            } else if ("stream".equals(name)) {
                streamType = searchEnum(StreamType.class, value);
            }
        }
    }

    public boolean isUseVoice() {
        return useVoice;
    }

    public String getUrl() {

        return url;
    }

    public String getText() {
        return text;
    }

    public StreamType getStreamType() {
        return streamType;
    }

    @Override
    public String toString() {
        return "Voice: " + useVoice + " Url: " + url + " Stream: " + streamType + " Text: " + text;
    }

    private static <T extends Enum<?>> T searchEnum(Class<T> enumeration, String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;
    }
}
