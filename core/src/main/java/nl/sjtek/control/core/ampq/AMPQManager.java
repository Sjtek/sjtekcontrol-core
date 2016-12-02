package nl.sjtek.control.core.ampq;

import com.google.common.eventbus.Subscribe;
import com.rabbitmq.client.*;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.events.DataChangedEvent;
import nl.sjtek.control.core.network.ResponseCache;
import nl.sjtek.control.data.actions.CustomAction;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by wouter on 30-11-16.
 */
public class AMPQManager {

    private static final String EXCHANGE_UPDATES = "updates";
    private static final String EXCHANGE_ACTIONS = "actions";

    private Channel channelAction;
    private Channel channelUpdate;
    private Connection connection;

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
        send(ResponseCache.getInstance().toJson());
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

        channelAction = connection.createChannel();
        channelAction.exchangeDeclare(EXCHANGE_ACTIONS, "fanout");
        String updateQueueName = channelAction.queueDeclare().getQueue();
        channelAction.queueBind(updateQueueName, EXCHANGE_ACTIONS, "");
        channelAction.basicConsume(updateQueueName, true, new ActionConsumer(channelAction));
        System.out.println("Connected to broker.");
    }

    private void send(String message) {
        try {
            channelUpdate.basicPublish(EXCHANGE_UPDATES, "", null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void disconnect() throws IOException, TimeoutException {
        channelUpdate.close();
        channelAction.close();
        connection.close();
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
            System.out.println("Received AMPQ action: " + path);
            Bus.post(new CustomAction(path));
        }
    }

}
