package nl.sjtek.sjtekcontrol.data;

public class Arguments {

    private boolean useVoice = false;
    private String url;

    public Arguments() {

    }

    public Arguments(String query) {
        if (query == null || query.isEmpty()) {
            return;
        }
        String splitted[] = query.split("&");

        for (int i = 0; i < splitted.length; i++) {
            if (splitted[i].equals("voice")) {
                this.useVoice = true;
                continue;
            }

            if (splitted[i].contains("url=")) {
                try {
                    String splittedUrl[] = splitted[i].split("=");
                    url = splittedUrl[i + 1];
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        }
    }

    public boolean isUseVoice() {
        return useVoice;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Voice: " + useVoice + " Url: " + url;
    }
}
