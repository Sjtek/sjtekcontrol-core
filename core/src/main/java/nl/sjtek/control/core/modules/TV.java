package nl.sjtek.control.core.modules;

import io.habets.javautils.Log;
import nl.sjtek.control.core.events.AudioEvent;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.network.Arguments;
import nl.sjtek.control.core.utils.Executor;
import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.responses.TVResponse;
import nl.sjtek.control.data.settings.User;

import java.io.IOException;

@SuppressWarnings("unused")
public class TV extends BaseModule {

    // https://github.com/ypid/lgcommander

    private static final String LGCOMMANDER_PATH = "/usr/bin/lgcommander";
    private static final String HOST = "10.10.0.20";
    private static final int PORT = 8080;
    private static final String KEY = "00000";
    private static final String PROTOCOL = "roap";
    private static final String DEBUG = TV.class.getSimpleName();

    public TV(String key) {
        super(key);
        new PingThread().start();
    }

    @Override
    public void onStateChanged(boolean enabled, User user) {
        if (!enabled) off(new Arguments());
    }

    private String[] getArgumentsForCommand(String command) {
        return new String[]{LGCOMMANDER_PATH, "-H " + HOST, "-p " + PORT, "-P " + PROTOCOL, "-k " + KEY, "-c " + command};
    }

    public void off(Arguments arguments) {
        new ExecuteThread("1");
    }

    public void volumelower(Arguments arguments) {
        new ExecuteThread("24");
    }

    public void volumeraise(Arguments arguments) {
        new ExecuteThread("25");
    }

    @Override
    public Response getResponse() {
        return new TVResponse();
    }

    @Override
    public String getSummaryText() {
        return "There is not much to tell about the TV.";
    }

    @Override
    public boolean isEnabled(String user) {
        return false;
    }

    private class PingThread extends Thread {

        private final String COMMAND[] = {
                "/bin/ping", "-c 1", HOST
        };
        private boolean isActive = false;

        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(2000);
                    int result = Executor.execute(COMMAND);
                    if (result == 0) {
                        if (!isActive) {
                            isActive = true;
                            Bus.post(new AudioEvent(getKey(), true));
                        }
                    } else {
                        if (isActive) {
                            isActive = false;
                            Bus.post(new AudioEvent(getKey(), false));
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    Log.e(DEBUG, "Error while pinging", e);
                }
            }
        }
    }

    private class ExecuteThread extends Thread {

        private final String[] command;

        public ExecuteThread(String command) {
            this.command = getArgumentsForCommand(command);
        }

        @Override
        public void run() {
            super.run();
            try {
                Executor.execute(command);
            } catch (IOException | InterruptedException e) {
                Log.e(DEBUG, "Failed to execute command", e);
            }
        }
    }
}
