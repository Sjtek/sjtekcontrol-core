package nl.sjtek.control.core.modules;

import nl.sjtek.control.data.responses.Response;
import nl.sjtek.control.data.responses.TimeResponse;

import java.util.Calendar;

/**
 * Created by wouter on 8-1-16.
 */
public class Time extends BaseModule {

    public Time(String key) {
        super(key);
    }

    @Override
    public Response getResponse() {
        return new TimeResponse(Calendar.getInstance().getTime().toString());
    }

    @Override
    public String getSummaryText() {
        return "It's" + Calendar.getInstance().getTime().toString();
    }
}
