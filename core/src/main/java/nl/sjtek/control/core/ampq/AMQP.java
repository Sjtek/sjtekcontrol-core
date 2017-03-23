package nl.sjtek.control.core.ampq;

import com.google.common.eventbus.Subscribe;
import com.rabbitmq.client.*;
import io.habets.javautils.Log;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.events.DataChangedEvent;
import nl.sjtek.control.core.network.ResponseCache;
import nl.sjtek.control.data.actions.CustomAction;
import nl.sjtek.control.data.ampq.events.LightEvent;
import nl.sjtek.control.data.ampq.events.LightStateEvent;
import nl.sjtek.control.data.ampq.events.SensorEvent;
import nl.sjtek.control.data.ampq.events.TemperatureEvent;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by wouter on 30-11-16.
 */
public class AMQP {

    public static final String EXCHANGE_TEMPERATURE = "temperature";
    private static final String DEBUG = AMQP.class.getSimpleName();
    private static final String EXCHANGE_UPDATES = "updates";
    private static final String EXCHANGE_ACTIONS = "actions";
    private static final String EXCHANGE_LIGHTS = "lights";
    private static final String EXCHANGE_LIGHTS_STATE = "lights_state";
    private static final String EXCHANGE_TOPIC = "amq.topic";
    private static final String EXCHANGE_SENSORS = "sensors";
    private Channel channelAction;
    private Channel channelUpdate;
    private Channel channelLights;
    private Connection connection;
    private Channel channelTemperature;
    private Channel channelLightsState;
    private Channel channelSensors;

    public AMQP() {
        try {
            connect();
        } catch (IOException | TimeoutException e) {
            Log.e(DEBUG, "Connection error", e);
        }
        Bus.regsiter(this);
    }

    @Subscribe
    public void onUpdate(DataChangedEvent event) {
        if (!event.shouldPushToClients()) return;
        try {
            channelUpdate.basicPublish(EXCHANGE_UPDATES, "", null, ResponseCache.getInstance().toJson().getBytes());
        } catch (IOException e) {
            Log.e(DEBUG, "Send error (" + event.getClass().getSimpleName() + ")" + e);
        }
    }

    @Subscribe
    public void onLightEvent(LightEvent lightEvent) {
        try {
            channelLights.basicPublish(EXCHANGE_LIGHTS, "", null, lightEvent.toString().getBytes());
        } catch (IOException e) {
            Log.e(DEBUG, "Send error (" + lightEvent.getClass().getSimpleName() + ")" + e);
        }
    }

    private void connect() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.10.0.1");
        factory.setUsername("core");
        factory.setPassword("yolo");
        factory.setAutomaticRecoveryEnabled(true);
        connection = factory.newConnection();

        channelUpdate = createExchange(EXCHANGE_UPDATES, BuiltinExchangeType.FANOUT);
        channelLights = createExchange(EXCHANGE_LIGHTS, BuiltinExchangeType.FANOUT);
        channelLightsState = createExchange(EXCHANGE_LIGHTS_STATE, BuiltinExchangeType.FANOUT);
        channelAction = createExchange(EXCHANGE_ACTIONS, BuiltinExchangeType.FANOUT);
        channelTemperature = createExchange(EXCHANGE_TEMPERATURE, BuiltinExchangeType.FANOUT);
        channelSensors = createExchange(EXCHANGE_SENSORS, BuiltinExchangeType.FANOUT);

        addConsumer(EXCHANGE_LIGHTS_STATE, new LightStateConsumer(channelLightsState));
        addConsumer(EXCHANGE_TEMPERATURE, new TemperatureConsumer(channelTemperature));
        addConsumer(EXCHANGE_ACTIONS, new ActionConsumer(channelAction));
        addConsumer(EXCHANGE_SENSORS, new SensorsConsumer(channelSensors));

        Channel channelTopic = connection.createChannel();
        channelTopic.exchangeDeclare(EXCHANGE_TOPIC, BuiltinExchangeType.TOPIC, true);
        channelTopic.exchangeBind(EXCHANGE_ACTIONS, EXCHANGE_TOPIC, EXCHANGE_ACTIONS);
        channelTopic.close();

        Log.i(DEBUG, "Connected to RabbitMQ");
    }

    private Channel createExchange(String exchange, BuiltinExchangeType type) throws IOException {
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchange, type);
        return channel;
    }

    private void addConsumer(String exchange, DefaultConsumer consumer) throws IOException {
        Channel channel = consumer.getChannel();
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchange, "");
        channel.basicConsume(queueName, true, consumer);
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
        public void handleDelivery(String consumerTag, Envelope envelope, com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws IOException {
            TemperatureEvent temperatureEvent = new TemperatureEvent(new String(body));
            Bus.post(temperatureEvent);
        }
    }

    private class LightStateConsumer extends DefaultConsumer {

        /**
         * Constructs a new instance and records its association to the passed-in channel.
         *
         * @param channel the channel to which this consumer is attached
         */
        public LightStateConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws IOException {
            LightStateEvent event = LightStateEvent.parseMessage(new String(body));
            if (event != null) Bus.post(event);
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
        public void handleDelivery(String consumerTag, Envelope envelope, com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws IOException {
            String path = new String(body);
            if (path.equals("ping")) return;
            Bus.post(new CustomAction(path));
        }
    }

    private class SensorsConsumer extends DefaultConsumer {
        public SensorsConsumer(Channel channelSensors) {
            super(channelSensors);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws IOException {
            SensorEvent event = SensorEvent.parseMessage(new String(body));
            if (event != null) Bus.post(event);
        }
    }
}
