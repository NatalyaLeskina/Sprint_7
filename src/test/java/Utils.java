import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class Utils {

    static int successCreateStatusCode = 201;
    static int successStatusCode = 200;
    static int failedConflictStatusCode = 409;
    static int failedClientStatusCode = 400;
    static int failedNotFoundStatusCode = 404;
    static boolean successOk = true;

    @Step("Check response message field")
    public static void checkResponseMessageField(Response response, String message) {
        response.then().assertThat().body("message", equalTo(message));
    }

    @Step("Send POST request to /api/v1/courier")
    static Response sendPostRequestCreateCourier(Courier courier) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
        return response;
    }

    @Step("Check status code")
    static void checkStatusCode(Response response, int statusCode) {
        response.then().assertThat().statusCode(statusCode);
    }

    static void deleteCourier(String login, String password) {
        Courier courier = new Courier(login, password);
        int id = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .path("id");
        Response response = given().when().delete("/api/v1/courier/" + id);
        response.then().assertThat().statusCode(successStatusCode);
    }
}
