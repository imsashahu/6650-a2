import com.google.gson.Gson;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MyRunnable implements Runnable{
    Connection connection;
    String queueName;
    ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> giving;
    ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> gotten;
    Gson gson = new Gson();
    public MyRunnable(Connection connection,
                      String queueName,
                      ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> giving,
                      ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> gotten
    ) {
        this.connection = connection;
        this.queueName = queueName;
        this.giving = giving;
        this.gotten = gotten;
    }

    @Override
    public void run() {
        try {
            Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
//            channel.exchangeDeclare("my-fanout-exchange", BuiltinExchangeType.FANOUT);
//            channel.queueBind(queueName, "my-fanout-exchange", "");

            // Consume message
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                SwipeDetails swipe = gson.fromJson(message, SwipeDetails.class);
                int swiper = swipe.getSwiper();
                int swipee = swipe.getSwipee();
                // String comment = swipe.getComment();

                giving.computeIfAbsent(swiper, k -> new CopyOnWriteArrayList<>()).add(swipee);
                gotten.computeIfAbsent(swipee, k -> new CopyOnWriteArrayList<>()).add(swiper);
                System.out.println(" [x] Received from " + swiper + " to " + swipee);
            };

            // Mark as delivered
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
