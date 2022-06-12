package сourierTest;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import clients.CourierClient;
import clients.RestAssuredClient;
import dto.CourierDto;
import dto.LoginDto;
import org.junit.After;
import org.junit.Test;
import testData.CourierTestData;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreateNewCourierTest {
    private static final CourierTestData courierData = new CourierTestData(); //Данные курьера. Предустановленные firstName, password, login
    private static CourierClient courierClient;
    public static CourierDto courierDto;
    public static String expectedMessage = "Этот логин уже используется";

    @Before
    public void setUp() {
        courierClient = new CourierClient(new RestAssuredClient());
        courierDto = new CourierDto();
        courierDto.setFirstName(courierData.firstName);
        courierDto.setLogin(courierData.login);
        courierDto.setPassword(courierData.password);
    }

    @After
    public void tearDown() {
        try {
            LoginDto loginDto = new LoginDto(courierData.login, courierData.password);
            int courierId = courierClient.getCourierId(loginDto);
            courierClient.delete(courierId);
        } catch (AssertionError exception) {
            System.out.println("Курьер не создан, нечего удалять"); // код, который выполнится, если произойдёт исключение AssertionError
        }
    }

    @Test
    @DisplayName("Создаем нового курьера и проверяем, что он создался")
    public void createNewCourierTestValid() {
        boolean isCreated = courierClient.create(courierDto)
                .then().statusCode(SC_CREATED)
                .extract()
                .path("ok");
        assertTrue(isCreated);
    }

    @Test
    @DisplayName("Попытка создать двух одинаковых курьеров")
    public void twoIdenticalCourierError() {
        courierClient.create(courierDto); //Создаем первого курьера
        Response responseCourierSecond = courierClient.create(courierDto); //создаем второго курьера
        assertEquals(SC_CONFLICT, responseCourierSecond.statusCode());
        assertEquals(expectedMessage, responseCourierSecond.path("message"));
    }

    @Test
    @DisplayName("Проверка статус кода")
    public void checkStatusCode() {
        Response responseCreate = courierClient.create(courierDto);
        assertEquals(SC_CREATED, responseCreate.statusCode());
    }
}
