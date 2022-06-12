package orderTest;
import dto.OrderDto;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import clients.OrderClient;
import clients.RestAssuredClient;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class FindOrderDataTest {
    private static OrderClient orderFind;
    public static OrderDto orderDto;
    private Number orderTrack;
    public static String expectedOrderMessage = "Заказ не найден";
    public static String expectedMessage = "Недостаточно данных для поиска";

    @Before
    public void setUp() {
        //данные для создания заказа
        OrderClient orderClient = new OrderClient(new RestAssuredClient());
        orderDto = new OrderDto();
        orderDto.setFirstName("Азатот");
        orderDto.setLastName("Демоновов");
        orderDto.setAddress("Подземная улица, дом 6 квартира 66.");
        orderDto.setMetroStation("4");
        orderDto.setPhone("+7 666 322 23 32");
        orderDto.setRentTime(4);
        orderDto.setDeliveryDate("2022-10-06");
        orderDto.setComment("!Обязательно позвонить!");
        orderDto.setColor(new String[]{"GREY"});
        Response createOrder = orderClient.create(orderDto);
        orderTrack = createOrder.path("track");   //запоминаем номер созданного заказа, для его поиска
    }


    @Test
    @DisplayName("Ищем существующий заказ по его номеру")
    public void findExistsOrder () {
        orderFind = new OrderClient(new RestAssuredClient());
        Response thisOrder = orderFind.getInfoOrder(orderTrack); //найдем наш заказ по номеру
        assertThat(thisOrder.path("order"), notNullValue()); //проверим что-что то вернулось на запрос, в order
        assertEquals( thisOrder.path("order.track"), orderTrack); //проверим, что мы нашли именно тот заказ, который искали
    }

    @Test
    @DisplayName("Ищем НЕ существующий заказ по его номеру")
    public void findNotExistsOrder () {
        orderFind = new OrderClient(new RestAssuredClient());
        orderFind.deleteOrder(orderTrack); //отменяем заказ, что бы был не существующий
        Response thisOrder = orderFind.getInfoOrder(orderTrack); //используем номер отмененного заказа
        assertEquals(SC_NOT_FOUND, thisOrder.statusCode());
        assertEquals(expectedOrderMessage, thisOrder.path("message"));
    }

    @Test
    @DisplayName("Ищем заказ без номера")
    public void findEmptyOrder () {
        orderFind = new OrderClient(new RestAssuredClient());
        Response thisOrder = orderFind.getInfoOrder(null); //Пытаемся передать пустой параметр
        assertEquals(SC_BAD_REQUEST, thisOrder.statusCode());
        assertEquals(expectedMessage, thisOrder.path("message"));
    }

}
