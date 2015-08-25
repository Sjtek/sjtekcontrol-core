package nl.sjtek.sjtekcontrol.devices;

public interface ArduinoEvent {
    void temperatureUpdate(float temperature);
    void buttonUpdate(Arduino.Button button);
}
