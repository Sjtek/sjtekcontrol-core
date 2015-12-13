package nl.sjtek.sjtekcontrol.network;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.sjtek.sjtekcontrol.modules.*;
import nl.sjtek.sjtekcontrol.settings.SettingsManager;
import nl.sjtek.sjtekcontrol.settings.User;
import nl.sjtek.sjtekcontrol.utils.Personalise;
import nl.sjtek.sjtekcontrol.utils.Speech;
import org.bff.javampd.exception.MPDConnectionException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;

@SuppressWarnings({"UnusedParameters", "unused"})
public class ApiHandler implements HttpHandler {

    public static final String CONTEXT = "/api";
    private static ApiHandler instance = new ApiHandler();
    private Music music;
    private Lights lights;
    private Temperature temperature;
    private TV tv;
    private Sonarr sonarr;
    private Quotes quotes;
    private NFC nfc;
    private NightMode nightMode;

    private int responseCode = 0;

    private ApiHandler() {
        System.out.print("Loading module");
        System.out.print(" music");
        try {
            this.music = new Music();
        } catch (UnknownHostException | MPDConnectionException e) {
            e.printStackTrace();
            this.music = null;
        }
        System.out.print(", lights");
        this.lights = new Lights();
        System.out.print(", temperature");
        this.temperature = new Temperature();
        System.out.print(",  tv");
        this.tv = new TV();
        System.out.print(", sonarr");
        this.sonarr = new Sonarr();
        System.out.print(", quotes");
        this.quotes = new Quotes();
        System.out.print(", NFC");
        this.nfc = new NFC();
        System.out.print(", NightMode");
        this.nightMode = new NightMode();

        if (music == null) {
            System.out.println("\nIn error state: music");
        }
        System.out.println();
    }

    public static ApiHandler getInstance() {
        return instance;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Arguments arguments = new Arguments(httpExchange.getRequestURI().getQuery());
        String fullPath = httpExchange.getRequestURI().getPath().toLowerCase();
        System.out.println();
        System.out.println(httpExchange.getRemoteAddress().toString() + " | " +
                httpExchange.getRequestURI().getPath() + " | " + arguments.toString());
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
                        if (arguments.useVoice()) Speech.tellAboutModules(getAll());
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

                        if (classString.equals("music")) {
                            execute(arguments, methodString, music);
                        } else if (classString.equals(Lights.class.getSimpleName().toLowerCase())) {
                            execute(arguments, methodString, lights);
                        } else if (classString.equals(TV.class.getSimpleName().toLowerCase())) {
                            execute(arguments, methodString, tv);
                        } else if (classString.equals(NFC.class.getSimpleName().toLowerCase())) {
                            responseType = ResponseType.CLEAN;
                            execute(arguments, methodString, nfc);
                        } else if (classString.equals(NightMode.class.getSimpleName().toLowerCase())) {
                            execute(arguments, methodString, nightMode);
                        }
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                responseCode = 404;
            }
        }

        String response;
        if (responseCode == 200) {
            switch (responseType) {
                case DEFAULT: {
                    response = Response.create(getAll());
                }
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

        httpExchange.sendResponseHeaders(responseCode, response.getBytes().length);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    private void execute(Arguments arguments, String methodString, BaseModule executor) {
        if ("info".equals(methodString)) {
            executor.info(arguments);
            responseCode = 200;
            return;
        }
        Method method = null;
        try {
            method = executor.getClass().getDeclaredMethod(methodString, arguments.getClass());
        } catch (NoSuchMethodException | SecurityException e) {
            responseCode = 404;
        }

        if (method != null) {
            try {
                method.invoke(executor, arguments);
                responseCode = 200;
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                responseCode = 404;
            }
        }
    }

    public synchronized void masterToggle(Arguments arguments) {
        Arguments dummyArguments = new Arguments();
        User user = arguments.getUser();
        boolean isWouter = (user != null && user == User.WOUTER);
        if (!isOn(isWouter)) {
            if (user != null && arguments.useVoice()) Speech.speakAsync(Personalise.messageWelcome(user));
            lights.toggle1on(dummyArguments);
            lights.toggle2on(dummyArguments);
            if (isWouter) lights.toggle3on(dummyArguments);
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
            if (isWouter) lights.toggle3off(dummyArguments);
        }
    }

    public Music getMusic() {
        return music;
    }

    public Lights getLights() {
        return lights;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public TV getTv() {
        return tv;
    }

    public Sonarr getSonarr() {
        return sonarr;
    }

    public Quotes getQuotes() {
        return quotes;
    }

    public NFC getNfc() {
        return nfc;
    }

    public NightMode getNightMode() {
        return nightMode;
    }

    public BaseModule[] getAll() {
        return new BaseModule[]{
                music,
                lights,
                temperature,
                tv,
                sonarr,
                quotes,
                nfc,
                nightMode,
        };
    }

    public boolean isOn(boolean checkWouter) {
        return (music.isPlaying() || (lights.getToggle1() || lights.getToggle2() || (checkWouter && lights.getToggle3())));
    }

    private enum ResponseType {
        DEFAULT,
        CLEAN,
        SETTINGS
    }
}