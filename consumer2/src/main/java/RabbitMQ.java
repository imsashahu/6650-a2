import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RabbitMQ {
    private final static String QUEUE_NAME = "my-queue-2";
    private static final String HOST = "172.31.16.194";
    private static final String USER = "admin";
    private static final String PWD = "1234";
    private static final Integer THREAD_SIZE = 50;
    private static ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> givingList = new ConcurrentHashMap<>();
    // A list of whom the user has swiped right on, max of 100

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setUsername(USER);
        factory.setPassword(PWD);

        Connection connection;
        try {
            connection = factory.newConnection();

        } catch (IOException | TimeoutException e) {
            e.getStackTrace();
            return;
        }

        // Start multithreading
        for (int i = 0; i < THREAD_SIZE; i++) {
            Runnable thread =  new MyRunnable(
                    connection,
                    QUEUE_NAME,
                    givingList,
                    i);
            new Thread(thread).start();
        }
    }

}
