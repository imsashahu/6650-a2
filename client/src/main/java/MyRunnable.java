import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;
import java.util.concurrent.CountDownLatch;

public class MyRunnable implements Runnable {
    static final String PATH = "http://52.35.131.11:8080/swipe_war";
    // http://server-load-balancer-1932552343.us-west-2.elb.amazonaws.com/swipe_war // load balancer
    // http://localhost:8080/swipe_war_exploded
    int numRequest;
    CountDownLatch countDown;

    public MyRunnable(int numRequest, CountDownLatch countDown) {
        this.numRequest = numRequest;
        this.countDown = countDown;
    }

    public void run() {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(PATH);
        apiClient.setConnectTimeout(500000);
        apiClient.setReadTimeout(500000);
        SwipeApi apiInstance = new SwipeApi(apiClient);
        SwipeDetails body = new SwipeDetails();
        RandomGenerator newPostRequest = new RandomGenerator();

        for (int iRequest = 0; iRequest < numRequest; iRequest++) {
            newPostRequest.generate();
            body.setSwiper(newPostRequest.getSwiper());
            body.setSwipee(newPostRequest.getSwipee());
            body.setComment(newPostRequest.getComment());
            String leftOrRight = newPostRequest.getSwipe();

            ApiResponse<Void> apiResponse = null;

            // Try once first + Else try another 5 times, in total 6 times
            int count = 0;
            while (count <= 5 && (apiResponse == null || apiResponse.getStatusCode() != 201)) {
                try {
                    apiResponse = apiInstance.swipeWithHttpInfo(body, leftOrRight);
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
                count ++;
            }

        }

        countDown.countDown();
    }

}
