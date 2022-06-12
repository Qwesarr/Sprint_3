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

public class DeleteCourierTest {
    private static final CourierTestData courierData = new CourierTestData(); //Данные курьера. Предустановленные firstName, password, login
    private static CourierClient courierClient;
    public static CourierDto courierDto;
    public static int courierId;
    public static String expectedCourierMessage = "Курьера с таким id нет";

    @Before
    public void setUp() {
        courierClient = new CourierClient(new RestAssuredClient());
        courierDto = new CourierDto();
        courierDto.setFirstName(courierData.firstName);
        courierDto.setLogin(courierData.login);
        courierDto.setPassword(courierData.password);
        courierClient.create(courierDto); //создаем курьера
        courierId = (courierClient.getCourierId(new LoginDto(courierData.login, courierData.password))); //берем ID нашего курьера
    }

    @After  //В конце, на всякий случай, будем пытаться удалить нашего курьера еще раз 
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
    @DisplayName("Пытаемся удалить курьера")
    public void deleteCourier() {
        Response deleteCourier = courierClient.delete(courierId);
        boolean isCreated = deleteCourier.path("ok");
        assertTrue(isCreated);
    }

    @Test
    @DisplayName("Пытаемся удалить курьера c не верным номером")
    public void deleteNotExistCourier() {
        courierClient.delete(courierId);  //Удаляем созданного курьера, что бы получить не верный номер курьера.
        Response deleteCourier = courierClient.delete(courierId);
        assertEquals(SC_NOT_FOUND, deleteCourier.statusCode());
        assertEquals(expectedCourierMessage, deleteCourier.path("message"));
    }

    @Test
    @DisplayName("Пытаемся удалить курьера без номера")
    public void deleteEmptyCourier() {
        Response deleteCourier = courierClient.delete();
        assertEquals(SC_BAD_REQUEST, deleteCourier.statusCode()); //удаляем без ID
        assertEquals(expectedCourierMessage, deleteCourier.path("message"));
    }

}
