import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.hamcrest.Matchers.*;

public class LoginCourierTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void checkLoginCourier() {
        String login = "newlogin" + nextInt(1000, 99999);
        String password = "password";

        Courier courier = new Courier(login, password);

        Utils.sendPostRequestCreateCourier(courier);
        System.out.println("login: " + login);

        Response loginResponse = sendPostRequestLoginCourier(courier);

        Utils.checkStatusCode(loginResponse, Utils.successStatusCode);
        checkResponseIdFieldExists(loginResponse);

        Utils.deleteCourier(login, password);
    }

    @Test
    public void checkImpossibleLoginWithoutRequiredField() {
        String login = "newlogin" + nextInt(1000, 99999);
        String password = "password";
        String responseMessage = "Недостаточно данных для входа";

        Courier courier = new Courier(login, password);
        Courier courierWithoutLogin = new Courier("", password);

        Utils.sendPostRequestCreateCourier(courier);

        Response response = sendPostRequestLoginCourier(courierWithoutLogin);

        Utils.checkStatusCode(response, Utils.failedClientStatusCode);
        Utils.checkResponseMessageField(response, responseMessage);

        Utils.deleteCourier(login, password);
    }

    @Test
    public void checkLoginWithWrongPassword() {
        String login = "newlogin" + nextInt(1000, 99999);
        String password = "password";
        String wrongPassword = "test";
        String responseMessage = "Учетная запись не найдена";

        Courier courier = new Courier(login, password);
        Courier wrongPasswordCourier = new Courier(login, wrongPassword);

        Utils.sendPostRequestCreateCourier(courier);

        Response response = sendPostRequestLoginCourier(wrongPasswordCourier);

        Utils.checkStatusCode(response, Utils.failedNotFoundStatusCode);
        Utils.checkResponseMessageField(response, responseMessage);

        Utils.deleteCourier(login, password);
    }

    @Step("Send POST request to /api/v1/courier/login")
    public Response sendPostRequestLoginCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Check response id field exists")
    public void checkResponseIdFieldExists(Response response) {
        response.then().assertThat().body("$", hasKey("id"));
    }
}
