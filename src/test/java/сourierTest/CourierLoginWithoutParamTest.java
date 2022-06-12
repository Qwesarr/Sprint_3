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

public class CourierLoginWithoutParamTest {
    private static final CourierTestData courierData = new CourierTestData(); //Данные курьера. Предустановленные firstName, password, login
    private static CourierClient courierClient;
    public static CourierDto courierDto;
    public static String expectedMessage = "Недостаточно данных для входа";
    private int courierId;

    @Before
    public void setUp() {
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
    @DisplayName("Пытаемся залогиниться без пароля")
    public void loginWithoutPass() {
        LoginDto loginDto = new LoginDto();
        loginDto.setLogin(courierDto.getLogin());
        Response responseLogin = courierClient.login(loginDto);
        //при попытке залогиниться без пароля(не передаем поле пароль), получаем ошибку 504
        assertEquals(SC_BAD_REQUEST, responseLogin.statusCode());
    }

    @Test
    @DisplayName("Пытаемся залогиниться с пустым паролем")
    public void loginWithEmptyPass() {
        LoginDto loginDto = new LoginDto();
        loginDto.setLogin(courierDto.getLogin());
        loginDto.setPassword("");
        Response responseLogin = courierClient.login(loginDto);
        assertEquals(SC_BAD_REQUEST, responseLogin.statusCode());
        assertEquals(expectedMessage, responseLogin.path("message"));
    }

    @Test
    @DisplayName("Пытаемся залогиниться без логина")
    public void loginWithoutLogin() {
        LoginDto loginDto = new LoginDto();
        loginDto.setPassword(courierDto.getPassword());
        Response responseLogin = courierClient.login(loginDto);
        assertEquals(SC_BAD_REQUEST, responseLogin.statusCode());
        assertEquals(expectedMessage, responseLogin.path("message"));
    }

    @Test
    @DisplayName("Пытаемся залогиниться с пустым логином")
    public void loginWithEmptyLogin() {
        LoginDto loginDto = new LoginDto();
        loginDto.setLogin("");
        loginDto.setPassword(courierDto.getPassword());
        Response responseLogin = courierClient.login(loginDto);
        assertEquals(SC_BAD_REQUEST, responseLogin.statusCode());
        assertEquals(expectedMessage, responseLogin.path("message"));
    }
}

