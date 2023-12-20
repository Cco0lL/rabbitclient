import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;
import lombok.val;
import ru.ccooll.rabbitclient.ClientFactory;
import ru.ccooll.rabbitclient.util.RoutingData;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        testDefaultClient();
        //testImpl();
        //topicExchangeTest();
    }

    private static void testDefaultClient() {
        ConnectionFactory factory = new ConnectionFactory() {
            {
                setUsername("kul");
                setPassword("verycool");
                setHost("localhost");
                setPort(5672);
            }
        };

        ClientFactory cf = ClientFactory.newInstance()
                .setConnectionFactory(factory);
        try (val client = cf.createNew("test", Executors.newFixedThreadPool(10,
                new ThreadFactoryBuilder().setNameFormat("client-worker-%d").build()))) {
            client.connect();

            val channel = client.createChannel();
            channel.declareQueue("test", true, false);
            val message = channel.convertAndSend(RoutingData.of("test"), "Hello", true);

            channel.addConsumer("test", String.class, mes -> {
                System.out.println(mes.message());
                val intList = new ArrayList<Integer>();
                for (int i = 0; i < 100; i++) {
                    intList.add(i + 1);
                }
                mes.sendResponseBatch(intList, true);
            });

            val batch = message.responseRequestBatch(Integer.class);
            System.out.println(batch.message().toString());
            System.out.println(batch.properties().getContentType());
            System.out.println(batch.properties().getDeliveryMode());

        } catch (Throwable th) {
            throw new IllegalStateException(th);
        }
    }

    static void topicExchangeTest() {
        ConnectionFactory factory = new ConnectionFactory() {
            {
                setUsername("kul");
                setPassword("verycool");
                setHost("localhost");
                setPort(5672);
            }
        };

        ClientFactory cf = ClientFactory.newInstance()
                .setConnectionFactory(factory);

        val latch = new CountDownLatch(2);
        try (val client = cf.createNewAndConnect("test", Executors.newFixedThreadPool(10,
                new ThreadFactoryBuilder().setNameFormat("client-worker-%d").build()))) {
            val channel = client.createChannel();
            //channel.qos(2, false);

            val exchangeName = "topic-test";
            channel.declareExchange(exchangeName, BuiltinExchangeType.TOPIC);

            val queue1Name = "test.t1";
            val queue2Name = "test.t2";
            channel.declareQueue(queue1Name, true);
            channel.declareQueue(queue2Name, true);
            channel.bindQueueToExchange(queue1Name, exchangeName, "test.*");
            channel.bindQueueToExchange(queue2Name, exchangeName, "test.*");

            channel.addConsumer("test.t1", String.class, (mes) -> {
                System.out.println(mes.message());
                latch.countDown();
                System.out.println(latch.getCount());
            });
            channel.addConsumer("test.t2", String.class, (mes) -> {
                System.out.println(mes.message());
                latch.countDown();
                System.out.println(latch.getCount());
            });

            val routingData = RoutingData.of(exchangeName, "test.7");
            channel.convertAndSend(routingData, "Hello World", false);

            latch.await();
            System.out.println("sup");
            channel.removeExchange("topic-test");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
