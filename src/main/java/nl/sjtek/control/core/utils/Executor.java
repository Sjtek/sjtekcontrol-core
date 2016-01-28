package nl.sjtek.control.core.utils;

import java.io.IOException;

public class Executor {

    /**
     * Execute a command.
     *
     * @param command
     * @throws IOException
     * @throws InterruptedException
     */
    public static void execute(String[] command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        int exitValue = process.exitValue();
        if (exitValue != 0) {
            System.err.println("Exit code: " + exitValue);
        }
    }

    public static void execute(String command) throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        process.waitFor();
        int exitValue = process.exitValue();
        if (exitValue != 0) {
            System.err.println("Exit code: " + exitValue);
        }
    }
}
