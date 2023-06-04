import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import pojo.Order;

import static io.restassured.RestAssured.given;

public class UserOrders extends StellarBurgerMainPage {
    private static final String ORDERS_PATH = "/api/orders";

    @Step("Send POST Request")
    public ValidatableResponse createOrder(Order body) {
        return given()
                .spec(getBaseSpec())
                .body(body)
                .when().log().everything()
                .post(ORDERS_PATH)
                .then().log().all();
    }

    @Step("Send POST Request")
    public ValidatableResponse createOrder(String token, Order body) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .body(body)
                .when().log().everything()
                .post(ORDERS_PATH)
                .then().log().all();
    }

    @Step("Send GET Request")
    public ValidatableResponse getUserOrders(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when().log().everything()
                .get(ORDERS_PATH)
                .then().log().all();
    }
}
