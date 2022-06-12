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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class CourierLoginTest {
    private static final CourierTestData courierDataFirst = new CourierTestData();  //Данные курьера. Предустановленные firstName, password, login
    private static CourierClient courierClient;
    private int courierId;
    public static CourierDto courierDto;
    String expectedMessage = "Учетная запись не найдена";


    @Before
    public void setUp() {
        courierClient = new CourierClient(new RestAssuredClient());
        courierDto = new CourierDto();
        courierDto.setFirstName(courierDataFirst.firstName);
        courierDto.setLogin(courierDataFirst.login);
        courierDto.setPassword(courierDataFirst.password);
        courierClient.create(courierDto);
    }

    @After
    public void tearDown() {
        try {
            LoginDto loginDto = new LoginDto(courierDataFirst.login, courierDataFirst.password);
            courierId = courierClient.getCourierId(loginDto);
            courierClient.delete(courierId);
        } catch (AssertionError exception) {
            System.out.println("Курьер не создан, нечего удалять"); // код, который выполнится, если произойдёт исключение AssertionError
        }
    }

    @Test
    @DisplayName("Проверяем успешный вход в систему")

    public void courierTrueLoginTest() {
        LoginDto loginDto = new LoginDto(courierDto.getLogin(), courierDto.getPassword());
        Response responseLogin = courierClient.login(loginDto);
        assertEquals(SC_OK, responseLogin.statusCode());
        courierId = responseLogin.path("id");
    }

    @Test
    @DisplayName("Проверяем не успешный вход в систему (нет пользователя)")
    public void courierFalseLoginTest() {
        LoginDto loginDto = new LoginDto(courierDto.getLogin() + 'w', courierDto.getPassword());
        Response responseLogin = courierClient.login(loginDto);
        assertEquals(SC_NOT_FOUND, responseLogin.statusCode());
        assertEquals(expectedMessage, responseLogin.path("message"));
    }

    @Test
    @DisplayName("Проверяем не успешный вход в систему (не верный пароль)")
    public void wrongPass() {
        LoginDto loginDto = new LoginDto(courierDto.getLogin(), courierDto.getPassword() + 1);
        Response responseLogin = courierClient.login(loginDto);
        assertEquals(SC_NOT_FOUND, responseLogin.statusCode());
        assertEquals(expectedMessage, responseLogin.path("message"));
    }

    @Test
    @DisplayName("Проверка, что успешный запрос возвращает id")
    public void idNotNull() {
        LoginDto loginDto = new LoginDto(courierDto.getLogin(), courierDto.getPassword());
        courierId = courierClient.login(loginDto)
                .then()
                .statusCode(SC_OK)
                .extract()
                .path("id");
        assertThat(courierId, notNullValue());
    }
}
