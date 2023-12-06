import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rabbitmq.client.ConnectionFactory;
import lombok.val;
import ru.ccooll.rabbitclient.Client;
import ru.ccooll.rabbitclient.ClientFactory;
import ru.ccooll.rabbitclient.connect.AutoReconnectStrategy;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;
import ru.ccooll.rabbitclient.message.properties.MutableMessageProperties;
import ru.ccooll.rabbitclient.message.properties.type.MessageTypeProperties;
import ru.ccooll.rabbitclient.util.RoutingData;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        testDefaultClient();
        //testImpl();
    }

    private static void testDefaultClient() {
        ConnectionFactory factory = new ConnectionFactory() {
            {
                setUsername("satarand");
                setPassword("somefortest");
                setHost("localhost");
                setPort(5672);
            }
        };

        ClientFactory cf = ClientFactory.newInstance()
                .setConnectionFactory(factory);
        try (val client = cf.createNewAndConnect("test", Executors.newFixedThreadPool(10,
                new ThreadFactoryBuilder().setNameFormat("client-worker-%d").build()))) {
            val countDownLatch = new CountDownLatch(1);
            val channel = client.createChannel();

            val message = channel.convertAndSend(RoutingData.of("test"), "Hello",
                    new MutableMessageProperties().messageTypeProperties(MessageTypeProperties.BINARY));

            message.responseRequestBatch(Integer.class)
                    .thenAccept((it) -> {
                        val list = it.message();
                        System.out.println(list.toString());
                        countDownLatch.countDown();
                    });

            channel.addConsumer("test", true, ((s, delivery) -> {
                val errorHandler = channel.errorHandler();
                val properties = delivery.getProperties();
                val deserialized = errorHandler.computeSafe(() ->
                        channel.deserializer().deserialize(delivery.getBody(), String.class));
                System.out.println(deserialized);
                val incoming = new IncomingMessage<>(channel, properties, deserialized);
                val intList = new ArrayList<Integer>();
                for (int i = 0; i < 100; i++) {
                    intList.add(i + 1);
                }
                incoming.sendResponseBatch(intList,
                        new MutableMessageProperties().messageTypeProperties(MessageTypeProperties.BINARY));
            }));

            countDownLatch.await();
        } catch (Throwable th) {
            throw new IllegalStateException(th);
        }
    }

    private static void testImpl() {
        ConnectionFactory factory = new ConnectionFactory() {
            {
                setUsername("guest");
                setPassword("guest");
                setHost("localhost");
                setPort(5672);
            }
        };
        ClientFactory cf = ClientFactory.newInstance()
                .setConnectionFactory(factory);

        val connectionStrategy = new AutoReconnectStrategy();
        Client client = null;
        try {
            client = connectionStrategy.connect("test", cf, Executors.newFixedThreadPool(10,
                    new ThreadFactoryBuilder().setNameFormat("client-worker-%d").build()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (client != null) {
            System.out.println("Successful connect");
            try {
                client.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }
}
