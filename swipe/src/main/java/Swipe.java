import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.servlet.annotation.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "swipe", value = "/swipe")
public class Swipe extends HttpServlet {
    private static final String QUEUE_NAME = "yh-queue";
    private static final String HOST = "52.24.24.233";
    private static final String USER = "admin";
    private static final String PWD = "1234";

    private Connection connection = null;

    @Override
    public void init() throws ServletException {
        super.init();

        // Connect to RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setUsername(USER);
        factory.setPassword(PWD);

        try {
            connection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.getStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        String urlPath = request.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Get missing parameters.");
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (!isUrlValid(urlParts)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Not valid URL: " + urlPath);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Get success: " + urlPath);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String urlPath = request.getPathInfo();

        // check we have a URL!
        if (urlPath == null || urlPath.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Post missing parameters.");
            return;
        }

        String[] urlParts = urlPath.split("/");

        if (!isUrlValid(urlParts)) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("Not valid URL: " + urlPath);
        } else {
            Gson gson = new Gson();
            try {
                StringBuilder sb = new StringBuilder();
                String s;
                while ((s = request.getReader().readLine()) != null) {
                    sb.append(s);
                }

                SwipeDetails requestFields = (SwipeDetails) gson.fromJson(sb.toString(), SwipeDetails.class);
                if ((requestFields.getSwipee() < 1 || requestFields.getSwipee() > 1000000)
                        || (requestFields.getSwiper() < 1 || requestFields.getSwiper() > 5000)
                        || (requestFields.getComment().length() != 256)) {
                    response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                    response.getWriter().write("Post values do not follow rules: " + urlPath);
                } else {
                    // Create payload
                    JsonObject payload = new JsonObject();
                    payload.addProperty("swiper", requestFields.getSwipee());
                    payload.addProperty("swipee", requestFields.getSwiper());
                    payload.addProperty("comment", requestFields.getComment());

                    // Connect to RabbitMQ
                    ConnectionFactory factory = new ConnectionFactory();
                    factory.setHost(HOST);
                    factory.setUsername(USER);
                    factory.setPassword(PWD);

                    if (connection == null) {
                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        response.getWriter().write("RabbitMQ not work.");
                    } else {
                        Channel channel = connection.createChannel();
                        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                        byte[] message = payload.toString().getBytes(StandardCharsets.UTF_8);
                        channel.basicPublish("", QUEUE_NAME, null, message);

                        response.setStatus(HttpServletResponse.SC_CREATED);
                        response.getWriter().write("Post works: " + urlPath);
                    }
                }
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                response.getWriter().write("Post Gson exception: " + ex + ": " + urlPath);
            }
        }
    }

    private boolean isUrlValid(String[] urlParts) {
        return urlParts.length >= 2
                && Objects.equals(urlParts[0], "")
                && (Objects.equals(urlParts[1], "left") || Objects.equals(urlParts[1], "right"));

    }

}
