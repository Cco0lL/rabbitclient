package ru.ccooll.rabbitclient.channel;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Delivery;
import org.jetbrains.annotations.Nullable;
import ru.ccooll.rabbitclient.common.Converter;
import ru.ccooll.rabbitclient.error.ErrorHandler;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingBatchMessage;
import ru.ccooll.rabbitclient.message.outgoing.OutgoingMessage;
import ru.ccooll.rabbitclient.message.properties.MutableMessageProperties;
import ru.ccooll.rabbitclient.util.RoutingData;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * represents an adapter of rabbit channel
 */
public interface AdaptedChannel {

    /**
     * declares an exchange, throws IOException that handles by
     * errors handler if shut down error or channel already closed,
     * or request of declare has expired,
     *
     * @param exchangeKey - exchange routing key
     * @param type        - rabbit exchange type
     */
    void declareExchange(String exchangeKey, BuiltinExchangeType type);

    /**
     * provides a way to declare an exchange with any params and arguments
     * <p>
     * @see com.rabbitmq.client.Channel#exchangeDeclare(String, BuiltinExchangeType, boolean, boolean, boolean, Map)
     * for more info
     */
    void declareExchange(String exchangeKey, BuiltinExchangeType type,
                         boolean durable, boolean autoDelete, boolean internal,
                         Map<String, Object> arguments);

    /**
     * declares a queue, throws IOException that handles by errors handler
     * if queue routing key length is longer than 255 symbols, or channel is already closed,
     * or request of declare has expired
     *
     * @param queueKey - routing key of queue
     * @param autoDelete - true if channel should delete when queue no longer in use
     */
    void declareQueue(String queueKey, boolean autoDelete);

    /**
     * provides a way to declare a queue with any params and arguments
     * <p>
     * @see com.rabbitmq.client.Channel#queueDeclare(String, boolean, boolean, boolean, Map) for
     * more info
     */
    void declareQueue(String queueKey, boolean durable, boolean exclusive,
                      boolean autoDelete, Map<String, Object> arguments);

    /**
     * removes an exchange, throws IOException that handles by errors handler
     * if channel already closed, or request of delete has expired
     *
     * @param exchangeKey - routing key of exchange
     */
    void removeExchange(String exchangeKey);

    /**
     * removes a queue  throws IOException that handles by errors handler
     * if queue routing key length longer than 255 or channel already closed,
     * or request of delete has expired
     *
     * @param queueKey - routing key of queue
     */
    void removeQueue(String queueKey);

    /**
     * binds queue to exchange with a specific routing key for this exchange throws IOException
     * that handles by errors handler if queue routing key length longer than 255 or channel
     * already closed, or request of bind has expired
     *
     * @param queueKey    - routing key of queue
     * @param exchangeKey - routing key of exchange
     * @param bindingKey  - routing key of binding
     */
    void bindQueueToExchange(String queueKey, String exchangeKey, String bindingKey);

    /**
     * unbinds queue to exchange with a specific routing key for this exchange throws IOException
     * that handles by errors handler if queue routing key length longer than 255 or channel
     * already closed, or request of bind has expired
     *
     * @param queueKey    - routing key of queue
     * @param exchangeKey - routing key of exchange
     * @param bindingKey  - routing key of binding
     */
    void unbindQueueToExchange(String queueKey, String exchangeKey, String bindingKey);

    /**
     * able to check if queue with specific routing key exists
     *
     * @param routingKey - routing key of queue
     * @return true if exists, false otherwise
     */
    boolean isQueueExist(String routingKey);

    /**
     * sends a message with raw body and returns an object
     * that able to request a response. This method destined to be able to
     * send any cached data
     *
     * @param routingData - routing data
     * @param body        - raw body in bytes
     * @return - outgoing data that able to request a response
     */
    OutgoingMessage send(RoutingData routingData, byte[] body);

    /**
     * converts sends a message and returns an object
     * that able to request a response
     *
     * @param routingData - routing data
     * @param message     - message object that will convert to raw bytes
     * @param properties  - properties of message
     * @return - outgoing data that able to request a response
     */
    OutgoingMessage convertAndSend(RoutingData routingData, Object message,
                                   MutableMessageProperties properties);

    /**
     * converts and sends a batch of messages and returns an
     * object that able to request a response
     *
     * @param routingData - routing data
     * @param messages    - messages that will convert to raw bytes
     * @param properties  - properties of batch
     * @return - outgoing batch data that able to request a response
     */
    OutgoingBatchMessage convertAndSend(RoutingData routingData, List<Object> messages,
                                        MutableMessageProperties properties);

    /**
     * adds consumer
     *
     * @param routingKey - routing key of consumes queue
     * @param autoAck    - auto acknowledge, it sets in false for callback consumer implementations, for
     *                   simple subscribe usage consider to set that in true
     * @param consumer   - consumer
     * @return - consumer's tag for able to remove consumer, returns null if an error has been occurred
     */
    @Nullable String addConsumer(String routingKey, boolean autoAck, BiConsumer<String, Delivery> consumer);

    /**
     * removes consumer
     *
     * @param consumerTag - tag of consumer that must be removed
     */
    void removeConsumer(String consumerTag);

    /**
     * acknowledges message with specific deliveryTag
     *
     * @param deliveryTag - delivery tag of received message
     * @param multiple    - if true, that acknowledges all previous acknowledged messages in specified consumer
     */
    void ack(long deliveryTag, boolean multiple);

    Converter converter();

    ErrorHandler errorHandler();

    /**
     * closes channel
     */
    void close();
}
