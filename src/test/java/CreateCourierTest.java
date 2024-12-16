import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.hamcrest.Matchers.equalTo;

import io.qameta.allure.Step;

public class CreateCourierTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void checkCreateCourier() {
        String login = "newlogin" + nextInt(1000, 99999);
        String password = "password";
        String firstName = "firstName";

        Courier courier = new Courier(login, password, firstName);

        Response response = Utils.sendPostRequestCreateCourier(courier);

        Utils.checkStatusCode(response, Utils.successCreateStatusCode);
        checkOkField(response, Utils.successOk);

        Utils.deleteCourier(login, password);
    }

    @Test
    public void checkImpossibleCreatingIdenticalCouriers() {
        String login = "newlogin" + nextInt(1000, 99999);
        String password = "password";
        String firstName = "firstName";
        String responseMessage = "Этот логин уже используется. Попробуйте другой.";

        Courier courier = new Courier(login, password, firstName);

        Response response = Utils.sendPostRequestCreateCourier(courier);
        Utils.checkStatusCode(response, Utils.successCreateStatusCode);

        Response response2 = Utils.sendPostRequestCreateCourier(courier);
        Utils.checkStatusCode(response2, Utils.failedConflictStatusCode);
        Utils.checkResponseMessageField(response2, responseMessage);

        Utils.deleteCourier(login, password);
    }

    @Test
    public void checkCreateWithOnlyRequiredFields() {
        String login = "newlogin" + nextInt(1000, 99999);
        String password = "password";

        Courier courier = new Courier(login, password);

        Response response = Utils.sendPostRequestCreateCourier(courier);
        Utils.checkStatusCode(response, Utils.successCreateStatusCode);

        Utils.deleteCourier(login, password);
    }

    @Test
    public void checkImpossibleCreatingWithoutRequiredField() {
        String login = "newlogin" + nextInt(1000, 99999);
        String responseMessage = "Недостаточно данных для создания учетной записи";

        Courier courier = new Courier(login);

        Response response = Utils.sendPostRequestCreateCourier(courier);

        Utils.checkStatusCode(response, Utils.failedClientStatusCode);
        Utils.checkResponseMessageField(response, responseMessage);
    }

    @Step("Check ok field")
    private void checkOkField(Response response, boolean ok) {
        response.then().assertThat().body("ok", equalTo(ok));
    }
}
