package nl.sjtek.sjtekcontrol.utils;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by wouter on 13-6-15.
 */
public class Speech {

    private static final String TEXT_TO_SPEECH_SERVICE =
            "http://translate.google.com/translate_tts";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) " +
                    "Gecko/20100101 Firefox/11.0";

    public static void main(String[] args) throws Exception {
        speech("How do you do Samantha?");
    }

    private static void download(String text, File output) throws IOException {
        String strUrl = TEXT_TO_SPEECH_SERVICE + "?" +
                "tl=" + "EN" + "&q=" + text;
        URL url = new URL(strUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("User-Agent", USER_AGENT);
        connection.connect();

        BufferedInputStream bufIn =
                new BufferedInputStream(connection.getInputStream());
        byte[] buffer = new byte[1024];
        int n;
        ByteArrayOutputStream bufOut = new ByteArrayOutputStream();
        while ((n = bufIn.read(buffer)) > 0) {
            bufOut.write(buffer, 0, n);
        }

        BufferedOutputStream out =
                new BufferedOutputStream(new FileOutputStream(output));
        out.write(bufOut.toByteArray());
        out.flush();
        out.close();
    }

    public static synchronized void speech(String text) {
        try {
            text = URLEncoder.encode(text, "utf-8");
            File soundFile = new File("/tmp/sjtekcontrol/voice/" + text + ".mp3");
            if (!soundFile.exists()) {
                boolean mkdirs = soundFile.getParentFile().mkdirs();
                download(text, soundFile);
            }
            AdvancedPlayer player = new AdvancedPlayer(new FileInputStream(soundFile));
            player.play();
            player.close();
        } catch (IOException | JavaLayerException e) {
            e.printStackTrace();
        }
    }

    public static void speechAsync(String text) {
        new SpeechAsyncThread(text).start();
    }

    private static class SpeechAsyncThread extends Thread {
        private final String text;

        public SpeechAsyncThread(String text) {
            this.text = text;
        }

        @Override
        public void run() {
            speech(text);
        }
    }
}
