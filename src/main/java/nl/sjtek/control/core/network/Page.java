package nl.sjtek.control.core.network;

public class Page {

    private Page() {
    }

    public static String getPage(int responseCode) {
        switch (responseCode) {
            case 404:
                return getNotFound();
            default:
                return getNotFound();
        }
    }

    public static int makeValid(int responseCode) {
        if (responseCode == 200 || responseCode == 404) {
            return responseCode;
        } else {
            return 500;
        }
    }

    private static String getNotFound() {
        return "<html><body><h1>404 Not Found</h1>No context found for request</body></html>";
    }

    private static String getInternalServerError() {
        return "<html><body><h1>500 Internal server error</h1>Oops, something went wrong in SjtekControl</body></html>";
    }
}
