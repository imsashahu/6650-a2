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
    private final static String QUEUE_NAME = "hello-queue";
    private static final String HOST = "52.12.73.41";
    private static final String USER = "admin";
    private static final String PWD = "1234";
    private static final Integer QUEUE_SIZE = 500000;
    private static final Integer THREAD_SIZE = 20;
    private static ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> giving = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, CopyOnWriteArrayList<Integer>> gotten = new ConcurrentHashMap<>();

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setUsername(USER);
        factory.setPassword(PWD);

        Connection connection;
        Channel channel;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        } catch (IOException | TimeoutException e) {
            e.getStackTrace();
            return;
        }

        // Start multithreading
        List<Integer> threadCounts = divideIntoThreads();
        for (int i = 0; i < THREAD_SIZE; i++) {
            Runnable thread =  new MyRunnable(
                    threadCounts.get(i),
                    channel,
                    QUEUE_NAME,
                    giving,
                    gotten);
            new Thread(thread).start();
        }
    }

    private static List<Integer> divideIntoThreads() {
        List<Integer> output = new java.util.ArrayList<>(Collections.nCopies(THREAD_SIZE, QUEUE_SIZE / THREAD_SIZE));
        if (QUEUE_SIZE % THREAD_SIZE == 0) {
            return output;
        }
        for (int i = 0; i < QUEUE_SIZE % THREAD_SIZE; i ++) {
            int index = new Random().nextInt(THREAD_SIZE);
            output.set(index, output.get(index) + 1);
        }
        return output;
    }

}
