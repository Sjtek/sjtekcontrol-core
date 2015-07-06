package nl.sjtek.sjtekcontrol.devices;

import org.json.JSONObject;

import java.util.Date;

public class Temperature {

    private Thread updateThread;
    private Date lastUpdate = new Date();
    private int temperature = 0;

    public Temperature() {
        this.updateThread = new Thread(new UpdateThread());
        this.updateThread.start();
    }

    public int getTemperature() {
        return temperature;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("temperature", temperature);
        jsonObject.put("lastUpdate", lastUpdate.toString());
        return jsonObject.toString();
    }

    private class UpdateThread implements Runnable {

        @Override
        public void run() {
            //TODO Update
            try {
                Thread.sleep(30000);
            } catch (InterruptedException ignored) { }

            lastUpdate = new Date();
            temperature = 0;
        }
    }
}
