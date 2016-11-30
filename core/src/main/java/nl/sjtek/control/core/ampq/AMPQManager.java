package nl.sjtek.control.core.ampq;

import com.google.common.eventbus.Subscribe;
import com.rabbitmq.client.*;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import nl.sjtek.control.core.events.Bus;
import nl.sjtek.control.core.events.DataChangedEvent;
import nl.sjtek.control.core.network.ResponseCache;
import nl.sjtek.control.data.ampq.events.ActionEvent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeoutException;

/**
 * Created by wouter on 30-11-16.
 */
public class AMPQManager {

    private static final String QUEUE_UPDATE = "update";
    private static final String QUEUE_ACTION = "actions";
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
        send(ResponseCache.getInstance().toJson());
    }

    private void connect() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("10.10.0.1");
        factory.setUsername("core");
        factory.setPassword("yolo");
        connection = factory.newConnection();
        channelUpdate = connection.createChannel();
        channelUpdate.queueDeclare(QUEUE_UPDATE, false, false, false, null);
        channelAction = connection.createChannel();
        channelAction.queueDeclare(QUEUE_ACTION, false, false, false, null);
        channelAction.basicConsume(QUEUE_ACTION, true, new ActionConsumer(channelAction));
    }

    private void send(String message) {
        try {
            channelUpdate.basicPublish("", QUEUE_UPDATE, null, message.getBytes());
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
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new ByteInputStream(body, body.length));
                ActionEvent action = (ActionEvent) inputStream.readObject();
                System.out.println(action.toString());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
