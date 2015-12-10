package nl.sjtek.sjtekcontrol.data;

import nl.sjtek.sjtekcontrol.utils.User;
import org.apache.commons.codec.Charsets;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONObject;

import java.util.List;

public class Arguments {

    private boolean useVoice = false;
    private String url = null;
    private String text = null;
    private StreamType streamType = StreamType.Stream;
    private MinecraftData minecraftData = new MinecraftData();
    private String cardId = null;
    private User user = null;
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
            } else if ("reactor".equals(name)) {
                minecraftData.setReactorRunning(value.equals("1"));
            } else if ("energystored".equals(name)) {
                try {
                    minecraftData.setEnergyStored(Float.valueOf(value));
                } catch (NumberFormatException e) {
                    minecraftData.setEnergyStored(-1);
                }
            } else if ("energyproducing".equals(name)) {
                try {
                    minecraftData.setEnergyProducing(Float.valueOf(value));
                } catch (NumberFormatException e) {
                    minecraftData.setEnergyProducing(-1);
                }
            } else if ("cardid".equals(name)) {
                cardId = value;
            } else if ("user".equals(name)) {
                user = User.valueOf(value.toUpperCase());
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

    public boolean isUseVoice() {
        return useVoice;
    }

    public Arguments setUseVoice(boolean useVoice) {
        this.useVoice = useVoice;
        return this;
    }

    public String getUrl() {
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
        }

        return ((url != null && !url.isEmpty()) ? prefix + url : null);
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

    public MinecraftData getMinecraftData() {
        return minecraftData;
    }

    public Arguments setMinecraftData(MinecraftData minecraftData) {
        this.minecraftData = minecraftData;
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

    @Override
    public String toString() {
        return "Voice: " + useVoice + " Url: " + url + " Stream: " + streamType + " Text: " + text;
    }

    public enum StreamType {
        Stream,
        YouTube,
        SoundCloud
    }

    public static class MinecraftData {
        private boolean reactorRunning = false;
        private float energyStored = -1;
        private float energyProducing = -1;

        public void setReactorRunning(boolean reactorRunning) {
            this.reactorRunning = reactorRunning;
        }

        public void setEnergyStored(float energyStored) {
            this.energyStored = energyStored;
        }

        public void setEnergyProducing(float energyProducing) {
            this.energyProducing = energyProducing;
        }

        public boolean isValid() {
            return (energyStored >= 0 && energyStored <= 100) &&
                    (energyProducing >= 0);
        }

        @Override
        public String toString() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("energyStored", energyStored);
            jsonObject.put("energyProducing", energyProducing);
            jsonObject.put("reactorRunning", reactorRunning);
            return jsonObject.toString();
        }
    }
}
