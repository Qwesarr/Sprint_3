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

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.junit.Assert.assertEquals;

public class CreateWithoutParamsTest {
    private static final CourierTestData courierData = new CourierTestData(); //Данные курьера. Предустановленные firstName, password, login
    private static String expectedMessage = "Недостаточно данных для создания учетной записи";
    private static CourierClient courierClient;
    public static CourierDto courierDto;

    @Before
    public void setUp() {
        courierClient = new CourierClient(new RestAssuredClient());
        courierDto = new CourierDto();
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
    @DisplayName("Создание курьера без логина")
    public void createWithoutLogin() {
        CourierDto courierDto = new CourierDto();
        courierDto.setFirstName(courierData.firstName);
        courierDto.setPassword(courierData.password);
        Response responseCreate = courierClient.create(courierDto);
        assertEquals(SC_BAD_REQUEST, responseCreate.statusCode());
        assertEquals(expectedMessage, responseCreate.path("message"));
    }



    @Test
    @DisplayName("Создание курьера без пароля")
    public void createWithoutPassword() {
        CourierDto courierDto = new CourierDto();
        courierDto.setFirstName(courierData.firstName);
        courierDto.setLogin(courierData.login);
        Response responseCreate = courierClient.create(courierDto);
        assertEquals(SC_BAD_REQUEST, responseCreate.statusCode());
        assertEquals(expectedMessage, responseCreate.path("message"));
    }
    @Test
    @DisplayName("Создание курьера без имени")
    public void createWithoutName() {
        CourierDto courierDto = new CourierDto();
        courierDto.setLogin(courierData.login);
        courierDto.setPassword(courierData.password);
        Response responseCreate = courierClient.create(courierDto);
        assertEquals(SC_BAD_REQUEST, responseCreate.statusCode());
        assertEquals(expectedMessage, responseCreate.path("message"));
    }
}
