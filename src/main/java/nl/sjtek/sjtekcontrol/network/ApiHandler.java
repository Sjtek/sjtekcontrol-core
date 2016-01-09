package nl.sjtek.sjtekcontrol.network;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.sjtek.sjtekcontrol.modules.*;
import nl.sjtek.sjtekcontrol.settings.SettingsManager;
import nl.sjtek.sjtekcontrol.settings.User;
import nl.sjtek.sjtekcontrol.utils.Personalise;
import nl.sjtek.sjtekcontrol.utils.Speech;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"UnusedParameters", "unused"})
public class ApiHandler implements HttpHandler {

    public static final String CONTEXT = "/api";
    private static ApiHandler instance = new ApiHandler();
    private Map<String, BaseModule> modules = new HashMap<>();

    private ApiHandler() {
        System.out.print("Loading modules:");

        System.out.println(" - music");
        Music musicNaspoleon = null;
        try {
            musicNaspoleon = new Music();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        modules.put("music", musicNaspoleon);

        System.out.println(" - lights");
        modules.put("lights", new Lights());
        System.out.println(" - temperature");
        modules.put("temperature", new Temperature());
        System.out.println(" - tv");
        modules.put("tv", new TV());
        System.out.println(" - sonarr");
        modules.put("sonarr", new Sonarr());
        System.out.println(" - quotes");
        modules.put("quotes", new Quotes());
        System.out.println(" - NFC");
        modules.put("nfc", new NFC());
        System.out.println(" - NightMode");
        modules.put("nightmode", new NightMode());
        System.out.println(" - Time");
        modules.put("time", new Time());

        System.out.println();
    }

    public static ApiHandler getInstance() {
        return instance;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        long start = System.currentTimeMillis();
        int responseCode = 0;
        Arguments arguments = new Arguments(httpExchange.getRequestURI().getQuery());
        String fullPath = httpExchange.getRequestURI().getPath().toLowerCase();
        System.out.println();
        System.out.println(httpExchange.getRemoteAddress().toString() + " | " +
                httpExchange.getRequestURI().getPath() + " | " +
                httpExchange.getRequestURI().getQuery());
        String splittedPath[] = fullPath.split("/");

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
            } catch (ArrayIndexOutOfBoundsException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | NullPointerException e) {
                responseCode = 404;
            }
        }

        responseCode = Page.makeValid(responseCode);
        String response;
        if (responseCode == 200) {
            switch (responseType) {
                case DEFAULT:
                    response = Response.create(modules);
                    break;
                case CLEAN:
                    response = "{ }";
                    break;
                case SETTINGS:
                    response = SettingsManager.getInstance().toString();
                    break;
                default:
                    response = "{ }";
            }
        } else {
            response = Page.getPage(responseCode);
        }

        long stop = System.currentTimeMillis();
        System.out.println("Response " + responseCode + " " + responseType + " " + (stop - start) + "ms");
        httpExchange.sendResponseHeaders(responseCode, response.getBytes().length);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
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
        Lights lights = getLights();
        Music music = getMusic();
        NightMode nightMode = getNightMode();

        Arguments dummyArguments = new Arguments();
        User user = arguments.getUser();
        boolean checkExtra = false;
        if (user != null) checkExtra = user.isCheckExtraLight();
        if (!isOn(checkExtra)) {
            if (user != null && arguments.useVoice()) Speech.speakAsync(Personalise.messageWelcome(user));
            lights.toggle1on(dummyArguments);
            lights.toggle2on(dummyArguments);
            if (checkExtra) lights.toggle3on(dummyArguments);
            if (!nightMode.isEnabled()) {
                if (user != null) {
                    // music.start(new Arguments().setUrl(user.getMusic()));
                } else if (arguments.getUrl() != null && !arguments.getUrl().isEmpty()) {
                    music.start(arguments);
                }
            }
        } else {
            if (user != null && arguments.useVoice()) Speech.speakAsync(Personalise.messageLeave(user));
            music.pause(dummyArguments);
            lights.toggle1off(dummyArguments);
            lights.toggle2off(dummyArguments);
            if (checkExtra) lights.toggle3off(dummyArguments);
        }
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


    public boolean isOn(boolean checkExtra) {
        Lights lights = getLights();
        Music music = getMusic();
        return (music.isPlaying() || (lights.getToggle1() || lights.getToggle2() || (checkExtra && lights.getToggle3())));
    }

    private enum ResponseType {
        DEFAULT,
        CLEAN,
        SETTINGS
    }
}