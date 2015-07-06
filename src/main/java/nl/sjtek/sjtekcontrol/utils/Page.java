package nl.sjtek.sjtekcontrol.utils;

public class Page {

    private Page() { }

    public static String getPage(int responseCode) {
        switch (responseCode) {
            case 404: return getNotFound();
            default: return getNotFound();
        }
    }

    private static String getNotFound() {
        return "<html><body><h1>404 Not Found</h1>No context found for request</body></html>";
    }
}
