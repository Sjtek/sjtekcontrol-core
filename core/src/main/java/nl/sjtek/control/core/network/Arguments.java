package nl.sjtek.control.core.network;

import nl.sjtek.control.core.settings.SettingsManager;
import nl.sjtek.control.data.settings.User;
import org.apache.commons.codec.Charsets;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.util.List;

public class Arguments {

    private boolean useVoice = false;
    private String url = "";
    private String text = null;
    private StreamType streamType = StreamType.Stream;
    private String cardId = null;
    private User user = SettingsManager.getInstance().getDefaultUser();
    private String code = "";
    private String rgb = "";

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
            } else if ("cardid".equals(name)) {
                cardId = value;
            } else if ("user".equals(name)) {
                user = SettingsManager.getInstance().getUser(value);
            } else if ("code".equals(name)) {
                code = value;
            } else if ("rgb".equals(name)) {
                rgb = value;
            }
        }
    }

    private static <T extends Enum<?>> T searchEnum(Class<T> enumeration, String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().compareToIgnoreCase(search) == 0) {
                return each;
            }
        }
        return null;
    }

    public boolean useVoice() {
        return useVoice;
    }

    public Arguments setUseVoice(boolean useVoice) {
        this.useVoice = useVoice;
        return this;
    }

    public String getUrl() {
        if (url.isEmpty()) return url;
        String prefix = "";
        switch (getStreamType()) {
            case Stream:
                prefix = "";
                break;
            case YouTube:
                prefix = "yt:";
                break;
            case SoundCloud:
                prefix = "sc:";
                break;
            case Spotify:
                prefix = "";
                break;
        }

        return prefix + url;
    }

    public Arguments setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getText() {
        return text;
    }

    public Arguments setText(String text) {
        this.text = text;
        return this;
    }

    public StreamType getStreamType() {
        return streamType;
    }

    public Arguments setStreamType(StreamType streamType) {
        this.streamType = streamType;
        return this;
    }

    public String getCardId() {
        return cardId;
    }

    public Arguments setCardId(String cardId) {
        this.cardId = cardId;
        return this;
    }

    public User getUser() {
        return user;
    }

    public Arguments setUser(User user) {
        this.user = user;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRgb() {
        return rgb;
    }

    public Arguments setRgb(String rgb) {
        this.rgb = rgb;
        return this;
    }

    @Override
    public String toString() {
        return "Voice: " + useVoice + " Url: " + url + " Stream: " + streamType + " Text: " + text;
    }

    public enum StreamType {
        Stream,
        YouTube,
        SoundCloud,
        Spotify
    }
}
