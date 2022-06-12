package сourierTest;
import dto.CourierDto;
import dto.LoginDto;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import clients.CourierClient;
import clients.RestAssuredClient;
import testData.CourierTestData;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;

public class CreateIdenticalLoginCourierTest {

    private static final CourierTestData courierDataFirst = new CourierTestData(); //Данные курьера. Предустановленные firstName, password, login
    private static final CourierTestData courierDataSecond = new CourierTestData("Андрей", "999", courierDataFirst.login); //второй пользователь с таким же логином
    private static CourierClient courierClient;
    public static CourierDto courierDtoFirst;
    public static CourierDto courierDtoSecond;
    public static String expectedMessage = "Этот логин уже используется";

    @Before
    public void setUp() {
        courierClient = new CourierClient(new RestAssuredClient());
    }

    @After
    public void tearDown() {
        int courierId;
        try {
            LoginDto loginDto = new LoginDto(courierDataFirst.login, courierDataFirst.password);
            courierId = courierClient.getCourierId(loginDto);
            courierClient.delete(courierId);
        } catch (AssertionError exception) {
            System.out.println("Курьер не создан, нечего удалять"); // код, который выполнится, если произойдёт исключение AssertionError
        }
        try {
            LoginDto loginDto = new LoginDto(courierDataSecond.login, courierDataSecond.password); //удалим второго курьера, если он вдруг создался.
            courierId = courierClient.getCourierId(loginDto);
            courierClient.delete(courierId);
        } catch (AssertionError exception) {
            System.out.println("Курьер не создан, нечего удалять"); // код, который выполнится, если произойдёт исключение AssertionError
        }
    }



    @Test
    @DisplayName("Пытаемся создать курьера с логином, который уже есть")
    public void CreateIdenticalLoginCourierError() {
        courierDtoFirst = new CourierDto(courierDataFirst.login, courierDataFirst.password, courierDataFirst.firstName);
        courierClient.create(courierDtoFirst); //создаем первого курьера
        courierDtoSecond = new CourierDto(courierDataFirst.login, courierDataSecond.password, courierDataSecond.firstName); //создаем второго курьера с таким же логином
        Response responseCreateSecond = courierClient.create(courierDtoSecond);
        assertEquals(SC_CONFLICT, responseCreateSecond.statusCode());
        assertEquals(expectedMessage, responseCreateSecond.path("message"));
    }

}
