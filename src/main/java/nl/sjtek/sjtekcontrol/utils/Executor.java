package nl.sjtek.sjtekcontrol.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Executor {

    /**
     * Execute a command.
     * @param command
     * @return Output of the command
     * @throws IOException
     * @throws InterruptedException
     */
    public static String execute(String[] command) throws IOException, InterruptedException {
        System.out.println("Executing: " + Arrays.toString(command));
        StringBuffer output = new StringBuffer();

        Process process;
        process = Runtime.getRuntime().exec(command);
        process.waitFor();
        int exitValue = process.exitValue();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = "";
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }
        System.out.println("Exit code: " + exitValue);
        System.out.println(output.toString());
        return output.toString();
    }

    public static String execute(String command) throws IOException, InterruptedException {
        System.out.println("Executing: " + command);
        StringBuffer output = new StringBuffer();

        Process process;
        process = Runtime.getRuntime().exec(command);
        process.waitFor();
        int exitValue = process.exitValue();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = "";
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }
        System.out.println("Exit code: " + exitValue);
        System.out.println(output.toString());
        return output.toString();
    }
}
