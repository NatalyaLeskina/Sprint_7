import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final String[] color;

    public CreateOrderTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Object[][] getColors() {
        return new Object[][] {
                { new String[] {"BLACK"} },
                { new String[] {"BLACK", "GREY"} },
                { new String[] {} }
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void checkCreateOrder() {

        Order order = new Order(color);

        Response response = sendPostRequestCreateOrder(order);

        Utils.checkStatusCode(response, Utils.successCreateStatusCode);
        checkResponseTrackFieldExists(response);
    }

    @Step("Send POST request to /api/v1/orders")
    private Response sendPostRequestCreateOrder(Order order) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders");
        return response;
    }

    @Step("Check response track field exists")
    private void checkResponseTrackFieldExists(Response response) {
        response.then().assertThat().body("$", hasKey("track"));
    }
}
