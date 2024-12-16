import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasKey;

public class GetOrdersListTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void checkGetOrdersList() {
        Response response = sendGetRequestOrdersList();
        System.out.println("response: " + response.asString());
        Utils.checkStatusCode(response, Utils.successStatusCode);
        checkResponseHasOrders(response);
    }

    @Step("Send GET request to /api/v1/orders")
    private Response sendGetRequestOrdersList() {
        return given().get("/api/v1/orders");
    }

    @Step("Check response has orders")
    private void checkResponseHasOrders(Response response) {
        response.then().assertThat().body("orders[0]", hasKey("id")) ;
    }
}
