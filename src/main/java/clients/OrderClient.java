package clients;


import dto.OrderDto;
import io.restassured.response.Response;

public class OrderClient {
    private final RestAssuredClient restAssuredClient;

    public OrderClient(RestAssuredClient restAssuredClient) {
        this.restAssuredClient = restAssuredClient;
    }

    public Response gettingList() {
        return restAssuredClient.get("orders");
    }

    public Response create(OrderDto orderDto) {
        return restAssuredClient.post("orders", orderDto);
    }

    public Response sendOrder(Number order, Number courierId) {
        return restAssuredClient.put("orders/accept/", order, courierId);
    }
    public Response sendOrder(Number courierId) {
        return restAssuredClient.put("orders/accept/", courierId);
    }

    public Response getInfoOrder(Number order) {
        return restAssuredClient.get("orders/track", order);
    }

    public void deleteOrder(Number track) {
        restAssuredClient.deleteOrder("orders/cancel/", track)
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .path("ok");
    }

}