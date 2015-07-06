package nl.sjtek.sjtekcontrol.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.sjtek.sjtekcontrol.data.Arguments;
import nl.sjtek.sjtekcontrol.utils.Page;

import java.io.*;

public class RootHandler implements HttpHandler {

    public static final String CONTEXT = "/";
    public static final String PATH = "/var/sjtekcontrol/www";
    public static final String DEFAULT_FILE = "/index.html";
    public static final int MAX_SIZE = 1000000;
    private String htmlFile = "";

    public RootHandler() {

        System.out.print("Loading " + PATH + DEFAULT_FILE + "... ");
        htmlFile = load(PATH + DEFAULT_FILE);
        if (htmlFile != null && !htmlFile.isEmpty()) {
            System.out.println("done");
        } else {
            System.out.println("failed");
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Arguments arguments = new Arguments(httpExchange.getRequestURI().getQuery());
        System.out.println();
        System.out.println(httpExchange.getRemoteAddress().toString() + " | " +
                httpExchange.getRequestURI().getPath() + " | " + arguments.toString());
        String path = httpExchange.getRequestURI().getPath();
        int responseCode;
        String response;
        if (path.equals("/") || path.equals("index.html")) {
            response = htmlFile;
            responseCode = 200;
        } else {
            responseCode = 404;
            response = Page.getPage(responseCode);
        }
        httpExchange.sendResponseHeaders(responseCode, response.getBytes().length);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    private String load(String path) {
        String content = "";
        File file = new File(path);
        if (file.exists() && file.canRead()) {
            try {
                BufferedInputStream bufIn =
                        new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = new byte[MAX_SIZE];
                int n;
                ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
                while ((n = bufIn.read(buffer)) > 0) {
                    bufOut.write(buffer, 0, n);
                }
                content = bufOut.toString();
                bufIn.close();
            } catch (Exception e) {
                content = "";
            }

        }
        return content;
    }
}
