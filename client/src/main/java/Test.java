import io.swagger.client.*;
import io.swagger.client.auth.*;
import io.swagger.client.model.*;
import io.swagger.client.api.SwipeApi;

import java.io.File;
import java.util.*;

public class Test {

    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath("http://localhost:8080/swipe_war_exploded");
        SwipeApi apiInstance = new SwipeApi(apiClient);
        SwipeDetails body = new SwipeDetails(); // SwipeDetails | response details
        body.setSwiper("123");
        body.setSwipee("456");
        body.setComment("y");
        String leftorright = "left";
        try {
            ApiResponse<Void> apiResponse = apiInstance.swipeWithHttpInfo(body, leftorright);
            int statusCode = apiResponse.getStatusCode();
            System.out.format("Status Code: %d\n", statusCode);
        } catch (ApiException e) {
            System.err.println("Exception when calling SwipeApi#swipe");
            e.printStackTrace();
        }
    }
}