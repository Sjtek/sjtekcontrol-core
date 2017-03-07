package nl.sjtek.control.core.ampq;

import com.google.common.eventbus.Subscribe;
import com.rabbitmq.client.*;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.events.DataChangedEvent;
import nl.sjtek.control.core.network.ResponseCache;
import nl.sjtek.control.data.actions.CustomAction;
import nl.sjtek.control.data.ampq.events.LightEvent;
import nl.sjtek.control.data.ampq.events.TemperatureEvent;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by wouter on 30-11-16.
 */
public class AMPQManager {

    public static final String EXCHANGE_TEMPERATURE = "temperature";
    private static final String EXCHANGE_UPDATES = "updates";
    private static final String EXCHANGE_ACTIONS = "actions";
    private static final String EXCHANGE_LIGHTS = "lights";
    private Channel channelAction;
    private Channel channelUpdate;
    private Channel channelLights;
    private Connection connection;
    private Channel channelTemperature;

    public AMPQManager() {
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        Bus.regsiter(this);
    }

    @Subscribe
    public void onUpdate(DataChangedEvent event) {
        if (!event.shouldPushToClients()) return;
        System.out.println("Sending update");
        try {
            channelUpdate.basicPublish(EXCHANGE_UPDATES, "", null, ResponseCache.getInstance().toJson().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onLightEvent(LightEvent lightEvent) {
        try {
            channelLights.basicPublish(EXCHANGE_LIGHTS, "", null, lightEvent.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connect() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.10.0.1");
        factory.setUsername("core");
        factory.setPassword("yolo");
        factory.setAutomaticRecoveryEnabled(true);
        connection = factory.newConnection();

        channelUpdate = connection.createChannel();
        channelUpdate.exchangeDeclare(EXCHANGE_UPDATES, "fanout");

        channelLights = connection.createChannel();
        channelLights.exchangeDeclare(EXCHANGE_LIGHTS, "fanout");

        channelAction = connection.createChannel();
        channelAction.exchangeDeclare(EXCHANGE_ACTIONS, "fanout");
        String updateQueueName = channelAction.queueDeclare().getQueue();
        channelAction.queueBind(updateQueueName, EXCHANGE_ACTIONS, "");
        channelAction.basicConsume(updateQueueName, true, new ActionConsumer(channelAction));

        channelTemperature = connection.createChannel();
        channelTemperature.exchangeDeclare(EXCHANGE_TEMPERATURE, "fanout");
        String temperatureQueueName = channelTemperature.queueDeclare().getQueue();
        channelTemperature.queueBind(temperatureQueueName, EXCHANGE_TEMPERATURE, "");
        channelTemperature.basicConsume(temperatureQueueName, true, new TemperatureConsumer(channelTemperature));


        System.out.println("Connected to broker.");
    }


    private void disconnect() throws IOException, TimeoutException {
        channelUpdate.close();
        channelAction.close();
        channelLights.close();
        connection.close();
    }

    private static class TemperatureConsumer extends DefaultConsumer {

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public TemperatureConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            TemperatureEvent temperatureEvent = new TemperatureEvent(new String(body));
            Bus.post(temperatureEvent);
        }
    }

    private class ActionConsumer extends DefaultConsumer {

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public ActionConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            String path = new String(body);
            if (path.equals("ping")) return;
            System.out.println("Received AMPQ action: " + path);
            Bus.post(new CustomAction(path));
        }
    }

}
