package orderTest;
import dto.OrderDto;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import clients.OrderClient;
import clients.RestAssuredClient;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class OrderListTest {
    private static OrderClient orderClient;
    public static OrderDto orderDto;

    @Before
    public void setUp() {
        orderClient = new OrderClient(new RestAssuredClient());
        orderDto = new OrderDto();
    }

    @Test
    @DisplayName("Проверяем список заказов (не пустой)")
    public void viewAllOrdersTest() {
        Response responseCreate = orderClient.gettingList();
        assertEquals(SC_OK, responseCreate.statusCode());
        assertThat(responseCreate.path("orders"), notNullValue());
    }
}


