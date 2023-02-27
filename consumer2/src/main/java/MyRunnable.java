import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyRunnable implements Runnable{
    Connection connection;
    String queueName;
    ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> givingList;
    Integer id;
    Gson gson = new Gson();

    public MyRunnable(Connection connection,
                      String queueName,
                      ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> givingList,
                      Integer id
    ) {
        this.connection = connection;
        this.queueName = queueName;
        this.givingList = givingList;
        this.id = id;
    }

    @Override
    public void run() {
        System.out.format("[Consumer-%d] Start consuming...\n", id);
        try {
            Channel channel = connection.createChannel();
            // channel.queueDeclare(queueName, false, false, false, null);
            channel.exchangeDeclare("my-fanout-exchange", BuiltinExchangeType.FANOUT);
            channel.queueBind(queueName, "my-fanout-exchange", "");

            // Consume message
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, StandardCharsets.UTF_8);
                    SwipeDetails swipe = gson.fromJson(message, SwipeDetails.class);
                    int swiper = swipe.getSwiper();
                    int swipee = swipe.getSwipee();
                    String direction = swipe.getDirection();

                    if (givingList.computeIfAbsent(swiper, k -> new CopyOnWriteArrayList<>()).size() < 100) {
                        givingList.get(swiper).add(swipee);
                    }
                    System.out.println(" [x] Received from " + swiper + " to " + swipee + " / " + direction);
                }
            };

            // Mark as delivered
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
