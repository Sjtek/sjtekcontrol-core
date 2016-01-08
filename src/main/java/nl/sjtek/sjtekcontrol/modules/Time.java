package nl.sjtek.sjtekcontrol.modules;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by wouter on 8-1-16.
 */
public class Time extends BaseModule {

    @Override
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("serverTime", Calendar.getInstance().getTime().toString());
        return jsonObject;
    }

    @Override
    public String getSummaryText() {
        return "It's" + Calendar.getInstance().getTime().toString();
    }
}
