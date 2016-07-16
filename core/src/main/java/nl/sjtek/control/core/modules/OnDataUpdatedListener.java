package nl.sjtek.control.core.modules;

/**
 * Created by wouter on 16-7-16.
 */
public interface OnDataUpdatedListener {
    void onUpdate(BaseModule module, String key, boolean send);
}
