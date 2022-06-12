package clients;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class RestAssuredClient {
    private RequestSpecification requestSpecification = new RequestSpecBuilder()
            .addFilter(new AllureRestAssured())
            .log(LogDetail.ALL)
            .setContentType(ContentType.JSON)
            .setBaseUri("https://qa-scooter.praktikum-services.ru/")
            .setBasePath("/api/v1/")
            .build();

    public RequestSpecification getRequestSpecification() {
        return requestSpecification;
    }

    public void setRequestSpecification(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public <T> Response post(String path, T body, Object... pathParams) {
        return given()
                .spec(getRequestSpecification())
                .body(body)
                .when()
                .post(path, pathParams);
    }

    public Response delete(String path) {
        return given()
                .spec(getRequestSpecification())
                .when()
                .delete(path);
    }

    public Response get(String path, Object... pathParams) {
        return given()
                .spec(getRequestSpecification())
                .when()
                .get(path, pathParams);
    }
    public Response put(String path, Number order, Number courier) {
        return given()
                .spec(getRequestSpecification())
                .queryParam("courierId", courier)
                .put(path + order);
    }
    public Response put(String path, Number courier) {
        return given()
                .spec(getRequestSpecification())
                .queryParam("courierId", courier)
                .put(path);
    }

    public Response deleteOrder(String path, Number track) {
        return given()
                .spec(getRequestSpecification())
                .queryParam("track", track)
                .put(path);
    }

    public Response get(String path, Number order) {
        return given()
                .spec(getRequestSpecification())
                .when()
                .queryParam("t", order)
                .get(path);
    }
    public Response get(String path, String order) {
        return given()
                .spec(getRequestSpecification())
                .when()
                .queryParam("t", order)
                .get(path);
    }
}