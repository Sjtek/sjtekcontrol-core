package nl.sjtek.sjtekcontrol.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import nl.sjtek.sjtekcontrol.data.Arguments;
import nl.sjtek.sjtekcontrol.devices.Lights;
import nl.sjtek.sjtekcontrol.devices.Music;
import nl.sjtek.sjtekcontrol.devices.TV;
import nl.sjtek.sjtekcontrol.devices.Temperature;
import nl.sjtek.sjtekcontrol.data.JsonResponse;
import nl.sjtek.sjtekcontrol.utils.Page;
import org.bff.javampd.exception.MPDConnectionException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;

public class ApiHandler implements HttpHandler {

    public static final String CONTEXT = "/api";

    private Music music;
    private Lights lights;
    private Temperature temperature;
    private TV tv;

    private int responseCode = 0;

    public ApiHandler() {
        try {
            this.music = new Music();
        } catch (UnknownHostException | MPDConnectionException e) {
            e.printStackTrace();
            this.music = null;
        }
        this.lights = new Lights();
        this.temperature = new Temperature();
        this.tv = new TV();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Arguments arguments = new Arguments(httpExchange.getRequestURI().getQuery());
        String fullPath = httpExchange.getRequestURI().getPath().toLowerCase();
        System.out.println();
        System.out.println(httpExchange.getRemoteAddress().toString() + " | " +
                httpExchange.getRequestURI().getPath() + " | " + arguments.toString());
        String splittedPath[] = fullPath.split("/");

        if (splittedPath.length < 3) {
            responseCode = 200;
        } else {

            try {
                String classString = splittedPath[2];
                if (classString.equals("info")) {
                    responseCode = 200;
                } else if (classString.equals("switch")) {
                    Arguments dummyArguments = new Arguments();
                    if (!music.isPlaying() && !lights.isOn()) {
                        music.start(dummyArguments);
                        lights.toggle1on(dummyArguments);
                        lights.toggle2on(dummyArguments);
                    } else {
                        music.pause(dummyArguments);
                        lights.toggle1off(dummyArguments);
                        lights.toggle2off(dummyArguments);
                    }
                    responseCode = 200;
                } else {
                    String methodString = splittedPath[3];

                    if (classString.equals(Music.class.getSimpleName().toLowerCase())) {
                        execMusic(arguments, methodString);
                    } else if (classString.equals(Lights.class.getSimpleName().toLowerCase())) {
                        execLights(arguments, methodString);
                    } else if (classString.equals(Temperature.class.getSimpleName().toLowerCase())) {
                        responseCode = 404;
                    } else if (classString.equals(TV.class.getSimpleName().toLowerCase())) {
                        responseCode = 404;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                responseCode = 404;
            }
        }

        String response;
        if (responseCode == 200) {
            response = JsonResponse.generate(music.toString(), lights.toString(), temperature.toString(), tv.toString());
        } else {
            response = Page.getPage(responseCode);
        }

        httpExchange.sendResponseHeaders(responseCode, response.getBytes().length);
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(response.getBytes());
        outputStream.close();
    }

    private void execMusic(Arguments arguments, String methodString) {
        Method method = null;
        try {
            method = music.getClass().getDeclaredMethod(methodString, arguments.getClass());
        } catch (NoSuchMethodException | SecurityException e) {
            responseCode = 404;
        }

        if (method != null) {
            try {
                method.invoke(music, arguments);
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
}