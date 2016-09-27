package nl.sjtek.control.core.events;

import com.google.common.eventbus.EventBus;


public class Bus {

    private static Bus instance = new Bus();
    private EventBus eventBus;

    private Bus() {
        eventBus = new EventBus();
    }

    public static Bus getInstance() {
        return instance;
    }

    public static void regsiter(Object object) {
        getInstance().getEventBus().register(object);
    }

    public static void unregister(Object object) {
        getInstance().getEventBus().unregister(object);
    }

    public static void post(Object object) {
        getInstance().getEventBus().post(object);
    }

    private EventBus getEventBus() {
        return eventBus;
    }
}
