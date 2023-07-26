import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rabbitmq.client.ConnectionFactory;
import lombok.val;
import ru.ccooll.rabbitclient.Client;
import ru.ccooll.rabbitclient.ClientFactory;
import ru.ccooll.rabbitclient.message.incoming.IncomingMessage;
import ru.ccooll.rabbitclient.util.MessagePropertiesUtils;
import ru.ccooll.rabbitclient.util.RoutingData;

import javax.print.DocFlavor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public class Main {

    public static void main(String[] args) {
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
        try (val client = cf.createNewAndConnect("test", Executors.newFixedThreadPool(10,
                new ThreadFactoryBuilder().setNameFormat("client-worker-%d").build()))) {
            val countDownLatch = new CountDownLatch(1);
            val channel = client.createChannel();
            val message = channel.prepareAndSend(RoutingData.of("test"), "Hello");
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
                incoming.sendResponseBatch(intList);
            }));

            countDownLatch.await();
        } catch (Throwable th) {
            throw new IllegalStateException(th);
        }
    }
}
