package clients;

import dto.CourierDto;
import dto.LoginDto;
import io.restassured.response.Response;

import static org.apache.http.HttpStatus.SC_OK;

public class CourierClient {
    private final RestAssuredClient restAssuredClient;

    public CourierClient(RestAssuredClient restAssuredClient) {
        this.restAssuredClient = restAssuredClient;
    }

    public Response login(LoginDto loginDto) {
        return restAssuredClient.post("courier/login", loginDto);
    }

    public Response create(CourierDto courierDto) {
        return restAssuredClient.post("courier", courierDto);
    }

    public Response delete(Integer courierId) {
        return restAssuredClient.delete("courier/" + courierId);
    }

    public Response delete() {
        return restAssuredClient.delete("courier/");
    }

    public int getCourierId(LoginDto loginDto) {
        return restAssuredClient.post("courier/login", loginDto)
                .then()
                .statusCode(200)
                .extract()
                .path("id");
    }
}