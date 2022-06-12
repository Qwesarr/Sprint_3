package orderTest;
import clients.CourierClient;
import dto.CourierDto;
import dto.LoginDto;
import dto.OrderDto;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import clients.OrderClient;
import clients.RestAssuredClient;
import testData.CourierTestData;


import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AcceptOrderTest {
    private static OrderClient orderClient;
    public static OrderDto orderDto;
    private static final CourierTestData courierData = new CourierTestData(); //Данные курьера. Предустановленные firstName, password, login
    private static CourierClient courierClient;
    private int courierId;
    private Number orderId;
    public static CourierDto courierDto;
    public static String expectedCourierMessage = "Курьера с таким id не существует";
    public static String expectedOrderMessage = "Заказа с таким id не существует";
    public static String expectedMessage = "Недостаточно данных для поиска";
    public static String expectedAcceptMessage = "Этот заказ уже в работе";

    @Before
    public void setUp() {
        //данные для создания заказа
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
        orderDto.setColor(new String[]{"GREY"});

        //Создаем курьера
        courierClient = new CourierClient(new RestAssuredClient());
        courierDto = new CourierDto();
        courierDto.setFirstName(courierData.firstName);
        courierDto.setLogin(courierData.login);
        courierDto.setPassword(courierData.password);
        courierClient.create(courierDto);
        }

        @After
        public void tearDown() {
            try {
                LoginDto loginDto = new LoginDto(courierData.login, courierData.password);
                courierId = courierClient.getCourierId(loginDto);
                courierClient.delete(courierId);
            } catch (AssertionError exception) {
                System.out.println("Курьер не создан, нечего удалять"); // код, который выполнится, если произойдёт исключение AssertionError
            }
        }


        @Test
        @DisplayName("Принимаем существующий заказ существующим курьером курьером")
        public void acceptExistsOrderExistsCourier () {
          Response createOrder = orderClient.create(orderDto);
          courierId = courierClient.getCourierId(new LoginDto(courierData.login, courierData.password)); //получаем ID курьера
          orderId = orderClient.getInfoOrder(createOrder.path("track")).path("order.id"); //получаем ID заказа
          Response sendOrder = orderClient.sendOrder(orderId,courierId);  //принимаем заказ курьером
          boolean isCreated = sendOrder.path("ok");
          assertTrue(isCreated);
        }

        @Test
        @DisplayName("Принимаем существующий заказ существующим курьером курьером, повторно")
        public void acceptExistsOrderExistsCourierTwice () {
            Response createOrder = orderClient.create(orderDto);
            courierId = courierClient.getCourierId(new LoginDto(courierData.login, courierData.password)); //получаем ID курьера
            orderId = orderClient.getInfoOrder(createOrder.path("track")).path("order.id"); //получаем ID заказа
            Response sendOrderFirst = orderClient.sendOrder(orderId,courierId);  //принимаем заказ курьером
            Response sendOrderSecond = orderClient.sendOrder(orderId,courierId);  //Еще раз принимаем этот же заказ
            assertEquals(SC_CONFLICT, sendOrderSecond.statusCode());
            assertEquals(expectedAcceptMessage, sendOrderSecond.path("message"));
        }

        @Test
        @DisplayName("Принимаем существующий заказ не существующим курьером курьером")
        public void acceptExistsOrderNotExistCourier () {
            Response createOrder = orderClient.create(orderDto);
            orderId = orderClient.getInfoOrder(createOrder.path("track")).path("order.id"); //получаем ID заказа
            Response sendOrder = orderClient.sendOrder(orderId,0);  //принимаем заказ курьером (которого нет)
            assertEquals(SC_NOT_FOUND, sendOrder.statusCode());
            assertEquals(expectedCourierMessage, sendOrder.path("message"));

        }

        @Test
        @DisplayName("Принимаем существующий заказ не указываем курьера")
        public void acceptExistsOrderEmptyCourier () {
            Response createOrder = orderClient.create(orderDto);
            orderId = orderClient.getInfoOrder(createOrder.path("track")).path("order.id"); //получаем ID заказа
            Response sendOrder = orderClient.sendOrder(orderId, null);  //принимаем заказ курьером (которого нет)
            assertEquals(SC_BAD_REQUEST, sendOrder.statusCode());
            assertEquals(expectedMessage, sendOrder.path("message"));

        }

        @Test
        @DisplayName("Принимаем не существующий заказ существующим курьером курьером")
        public void acceptNotExistsOrderExistCourier () {
            courierId = courierClient.getCourierId(new LoginDto(courierData.login, courierData.password)); //получаем ID курьер
            Response sendOrder = orderClient.sendOrder(0,courierId);  //принимаем заказ курьером
            assertEquals(SC_NOT_FOUND, sendOrder.statusCode());
            assertEquals(expectedOrderMessage,sendOrder.path("message"));
        }

        @Test
        @DisplayName("Принимаем заказ существующим курьером курьером, без номера заказа")
        public void acceptEmptyOrderExistCourier () {
            courierId = courierClient.getCourierId(new LoginDto(courierData.login, courierData.password)); //получаем ID курьера
            Response sendOrder = orderClient.sendOrder(courierId);  //принимаем заказ курьером (которого нет)
            assertEquals(SC_NOT_FOUND, sendOrder.statusCode());
            assertEquals(expectedMessage, sendOrder.path("message"));
        }
}
