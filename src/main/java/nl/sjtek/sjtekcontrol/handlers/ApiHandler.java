package nl.sjtek.sjtekcontrol.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.sjtek.sjtekcontrol.data.Arguments;
import nl.sjtek.sjtekcontrol.data.JsonResponse;
import nl.sjtek.sjtekcontrol.data.SettingsManager;
import nl.sjtek.sjtekcontrol.devices.*;
import nl.sjtek.sjtekcontrol.utils.Page;
import nl.sjtek.sjtekcontrol.utils.Personalise;
import nl.sjtek.sjtekcontrol.utils.Speech;
import nl.sjtek.sjtekcontrol.utils.User;
import org.bff.javampd.exception.MPDConnectionException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;

@SuppressWarnings("UnusedParameters")
public class ApiHandler implements HttpHandler {

    public static final String CONTEXT = "/api";
    private static ApiHandler instance = new ApiHandler();
    private Music music;
    private Lights lights;
    private Temperature temperature;
    private TV tv;
    private Sonarr sonarr;
    private Minecraft minecraft;
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
        System.out.print(", minecraft");
        this.minecraft = new Minecraft();
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
                        if (arguments.isUseVoice()) Speech.speakAsync("Settings reloaded");
                        responseType = ResponseType.SETTINGS;
                        responseCode = 200;
                        break;
                    default:
                        String methodString = splittedPath[3];

                        if (classString.equals("music")) {
                            execMusic(arguments, methodString, music);
                        } else if (classString.equals(Lights.class.getSimpleName().toLowerCase())) {
                            execLights(arguments, methodString);
                        } else if (classString.equals(Temperature.class.getSimpleName().toLowerCase())) {
                            responseCode = 404;
                        } else if (classString.equals(TV.class.getSimpleName().toLowerCase())) {
                            execTV(arguments, methodString);
                        } else if (classString.equals(Minecraft.class.getSimpleName().toLowerCase())) {
                            execMinecraft(arguments, methodString);
                        } else if (classString.equals(NFC.class.getSimpleName().toLowerCase())) {
                            responseType = ResponseType.CLEAN;
                            execNFC(arguments, methodString);
                        } else if (classString.equals(NightMode.class.getSimpleName().toLowerCase())) {
                            execNightMode(arguments, methodString);
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
                    String stringMusic = music.toString();
                    String stringLights = lights.toString();
                    String stringTemperature = temperature.toString();
                    String stringTv = tv.toString();
                    String stringSonarr = sonarr.toString();
                    String stringMinecraft = minecraft.toString();
                    String stringQuotes = quotes.toString();

                    response = JsonResponse.generate(stringMusic, stringLights, stringTemperature, stringTv, stringSonarr, stringMinecraft, stringQuotes);
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

    private void execMusic(Arguments arguments, String methodString, Music musicInstance) {
        Method method = null;
        try {
            method = Music.class.getDeclaredMethod(methodString, arguments.getClass());
        } catch (NoSuchMethodException | SecurityException e) {
            responseCode = 404;
        }

        if (method != null) {
            try {
                method.invoke(musicInstance, arguments);
                responseCode = 200;
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                responseCode = 404;
            }
        }
    }

    private void execLights(Arguments arguments, String methodString) {
        Method method = null;
        try {
            method = lights.getClass().getDeclaredMethod(methodString, arguments.getClass());
        } catch (NoSuchMethodException | SecurityException e) {
            responseCode = 404;
        }

        if (method != null) {
            try {
                method.invoke(lights, arguments);
                responseCode = 200;
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                responseCode = 404;
            }
        }
    }

    private void execTV(Arguments arguments, String methodString) {
        Method method = null;
        try {
            method = tv.getClass().getDeclaredMethod(methodString, arguments.getClass());
        } catch (NoSuchMethodException | SecurityException e) {
            responseCode = 404;
        }

        if (method != null) {
            try {
                method.invoke(tv, arguments);
                responseCode = 200;
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                responseCode = 404;
            }
        }
    }

    private void execMinecraft(Arguments arguments, String methodString) {
        Method method = null;
        try {
            method = minecraft.getClass().getDeclaredMethod(methodString, arguments.getClass());
        } catch (NoSuchMethodException | SecurityException e) {
            responseCode = 404;
        }

        if (method != null) {
            try {
                method.invoke(minecraft, arguments);
                responseCode = 200;
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                responseCode = 404;
            }
        }
    }

    private void execNFC(Arguments arguments, String methodString) {
        Method method = null;
        try {
            method = nfc.getClass().getDeclaredMethod(methodString, arguments.getClass());
        } catch (NoSuchMethodException | SecurityException e) {
            responseCode = 404;
        }

        if (method != null) {
            try {
                method.invoke(nfc, arguments);
                responseCode = 200;
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                responseCode = 404;
            }
        }
    }

    private void execNightMode(Arguments arguments, String methodString) {
        Method method = null;
        try {
            method = nightMode.getClass().getDeclaredMethod(methodString, arguments.getClass());
        } catch (NoSuchMethodException | SecurityException e) {
            responseCode = 404;
        }

        if (method != null) {
            try {
                method.invoke(nightMode, arguments);
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
            if (user != null && arguments.isUseVoice()) Speech.speakAsync(Personalise.messageWelcome(user));
            lights.toggle1on(dummyArguments);
            lights.toggle2on(dummyArguments);
            if (isWouter) lights.toggle3on(dummyArguments);
            if (!nightMode.isEnabled()) {
                if (user != null) {
//                    music.start(new Arguments().setUrl(user.getMusic()));
                } else if (arguments.getUrl() != null && !arguments.getUrl().isEmpty()) {
                    music.start(arguments);
                }
            }
        } else {
            if (user != null && arguments.isUseVoice()) Speech.speakAsync(Personalise.messageLeave(user));
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

    public Minecraft getMinecraft() {
        return minecraft;
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

    public boolean isOn(boolean checkWouter) {
        return (music.isPlaying() || (lights.getToggle1() || lights.getToggle2() || (checkWouter && lights.getToggle3())));
    }

    private enum ResponseType {
        DEFAULT,
        CLEAN,
        SETTINGS
    }
}