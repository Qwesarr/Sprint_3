package orderTest;
import dto.OrderDto;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import clients.OrderClient;
import clients.RestAssuredClient;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;


@RunWith(Parameterized.class)
public class CreateNewOrderTest {
    private static OrderClient orderClient;
    public static OrderDto orderDto;

    @Parameterized.Parameter
    public String[] color;

    @Parameterized.Parameters
    public static Object[] params() throws Exception {
        return new String[][][]{
                {{"BLACK", "GREY"}},
                {{"BLACK"}},
                {{"GREY"}},
                {null}
        };
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient(new RestAssuredClient());
        orderDto = new OrderDto();
        orderDto.setFirstName("Азатот");
        orderDto.setLastName("Демоновов");
        orderDto.setAddress("Подземная улица, дом 6 квартира 66.");
        orderDto.setMetroStation("4");
        orderDto.setPhone("+7 666 322 23 32");
        orderDto.setRentTime(4);
        orderDto.setDeliveryDate("2022-10-06");
        orderDto.setComment("!Обязательно позвонить!");
        orderDto.setColor(color);
    }



    @Test
    @DisplayName("Создаем заказы с выбором цвета самоката (или без цвета)")
    public void createOrdersWithParams() {
        Response responseCreate = orderClient.create(orderDto);
        assertEquals(SC_CREATED, responseCreate.statusCode());
        assertThat(responseCreate.path("track"), notNullValue());
    }
}
