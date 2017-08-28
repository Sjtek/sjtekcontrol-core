package nl.sjtek.control.core.network;

import com.google.common.eventbus.Subscribe;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.habets.javautils.Log;
import nl.sjtek.control.core.ampq.AMQP;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.events.StateEvent;
import nl.sjtek.control.core.modules.*;
import nl.sjtek.control.core.settings.SettingsManager;
import nl.sjtek.control.core.utils.Speech;
import nl.sjtek.control.data.actions.ActionInterface;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"UnusedParameters", "unused"})
public class ApiHandler implements HttpHandler {

    public static final String CONTEXT = "/api";
    private static final String DEBUG = ApiHandler.class.getSimpleName();
    private static ApiHandler instance = new ApiHandler();
    private Map<String, BaseModule> modules = new HashMap<>();
    private WSServer wsServer;
    private AMQP amqp;

    private ApiHandler() {

        ResponseCache.getInstance();

        Log.i(DEBUG, "Loading modules");

        modules.put("audio", new Audio("audio").init());
        modules.put("music", new Music("music").init());
        modules.put("lights", new Lights("lights").init());
        modules.put("temperature", new Temperature("temperature").init());
        modules.put("tv", new TV("tv").init());
        modules.put("sonarr", new Sonarr("sonarr").init());
        modules.put("quotes", new Quotes("quotes").init());
        modules.put("nfc", new NFC("nfc").init());
        modules.put("nightmode", new NightMode("nightmode").init());
        modules.put("time", new Time("time").init());
        modules.put("coffee", new Coffee("coffee").init());
        modules.put("screen", new Screen("screen").init());
        modules.put("art", new Art("art").init());
        modules.put("motion", new Motion("motion").init());

        Bus.regsiter(this);

        this.wsServer = new WSServer();
        this.wsServer.start();

        amqp = new AMQP();

        Log.i(DEBUG, "Handler ready");
    }

    public static ApiHandler getInstance() {
        return instance;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Arguments arguments = new Arguments(httpExchange.getRequestURI().getQuery());
        String fullPath = httpExchange.getRequestURI().getPath().toLowerCase();

        ExecuteResult executeResult = executePath(fullPath, arguments);
        String response = executeResult.getResponseText();

        httpExchange.sendResponseHeaders(executeResult.getResponseCode(), response.getBytes().length);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    @Subscribe
    public ExecuteResult executePath(ActionInterface action) {
        return executePath(CONTEXT + action.getPath());
    }

    public ExecuteResult executePath(String path) {
        return executePath(path, new Arguments());
    }

    public ExecuteResult executePath(String path, Arguments arguments) {
        long start = System.currentTimeMillis();
        int responseCode = 0;

        String splittedPath[] = path.split("/");

        ResponseType responseType = ResponseType.DEFAULT;

        if (splittedPath.length < 3) {
            responseCode = 200;
        } else {

            try {
                String classString = splittedPath[2];
                switch (classString) {
                    case "info":
                        responseCode = 200;
                        if (arguments.useVoice()) Speech.tellAboutModules(modules);
                        break;
                    case "switch":
                        masterToggle(arguments);
                        responseCode = 200;
                        break;
                    case "toggle":
                        masterToggle(arguments);
                        responseCode = 200;
                        break;
                    case "speech":
                        Speech.speak("" + arguments.getText());
                        responseCode = 200;
                        break;
                    case "reload":
                        SettingsManager.getInstance().reload();
                        if (arguments.useVoice()) Speech.speakAsync("Settings reloaded");
                        responseType = ResponseType.SETTINGS;
                        responseCode = 200;
                        break;
                    case "data":
                        responseType = ResponseType.DATA;
                        responseCode = 200;
                        break;
                    case "temp-log":
                        responseType = ResponseType.TEMP_LOG;
                        responseCode = 200;
                        break;
                    default:
                        String methodString = splittedPath[3];

                        BaseModule baseModule = modules.get(classString);
                        if (baseModule != null) {
                            execute(arguments, methodString, baseModule);
                            responseCode = 200;
                        } else {
                            throw new NullPointerException("No such module");
                        }

                        break;
                }
            } catch (ArrayIndexOutOfBoundsException | NoSuchMethodException | IllegalAccessException | NullPointerException e) {
                responseCode = 404;
            } catch (InvocationTargetException e) {
                responseCode = 500;
                Log.e(DEBUG, "Request error", e);
            }
        }

        responseCode = Page.makeValid(responseCode);
        String response;
        if (responseCode == 200) {
            switch (responseType) {
                case DEFAULT:
                    response = ResponseCache.getInstance().toJson();
                    break;
                case CLEAN:
                    response = "{ }";
                    break;
                case SETTINGS:
                    response = SettingsManager.getInstance().toString();
                    break;
                case DATA:
                    response = ResponseBuilder.createData();
                    break;
                case TEMP_LOG:
                    response = getTemperature().getLogData();
                    break;
                default:
                    response = "{ }";
            }
        } else {
            response = Page.getPage(responseCode);
        }

        long stop = System.currentTimeMillis();
        Log.i(DEBUG, path + "?" + arguments.getQuery() + " - " + (stop - start) + "ms");

        return new ExecuteResult(responseCode, response);
    }

    private void execute(Arguments arguments, String methodString, BaseModule executor)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if ("info".equals(methodString)) {
            executor.info(arguments);
            return;
        }
        Method method;
        method = executor.getClass().getDeclaredMethod(methodString, arguments.getClass());


        if (method != null) {
            method.invoke(executor, arguments);
        } else {
            throw new NullPointerException("Method not found");
        }
    }

    public synchronized void masterToggle(Arguments arguments) {
        Bus.post(new StateEvent(!isOn(arguments.getUserName()), arguments.getUser()));
    }

    public Music getMusic() {
        return (Music) modules.get("music");
    }

    public Lights getLights() {
        return (Lights) modules.get("lights");
    }

    public Temperature getTemperature() {
        return (Temperature) modules.get("temperature");
    }

    public TV getTv() {
        return (TV) modules.get("tv");
    }

    public Sonarr getSonarr() {
        return (Sonarr) modules.get("sonarr");
    }

    public Quotes getQuotes() {
        return (Quotes) modules.get("quotes");
    }

    public NFC getNfc() {
        return (NFC) modules.get("nfc");
    }

    public NightMode getNightMode() {
        return (NightMode) modules.get("nightmode");
    }


    public boolean isOn(String user) {
        for (Map.Entry<String, BaseModule> entry : modules.entrySet()) {
            if (entry.getValue().isEnabled(user)) return true;
        }

        return false;
    }

    private enum ResponseType {
        DEFAULT,
        CLEAN,
        SETTINGS,
        DATA,
        TEMP_LOG,
    }

    private class ExecuteResult {
        private final int responseCode;
        private final String responseText;

        public ExecuteResult(int responseCode, String responseText) {
            this.responseCode = responseCode;
            this.responseText = responseText;
        }

        public int getResponseCode() {
            return responseCode;
        }

        public String getResponseText() {
            return responseText;
        }
    }
}