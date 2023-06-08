import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import pojo.IngredientsResponse;
import pojo.Order;

import static io.restassured.RestAssured.given;

public class UserOrders extends StellarBurgerMainPage {

    private static final String API = "/api";
    private static final String ORDERS_PATH = API + "/orders";
    private static final String INGREDIENTS_PATH = API + "/ingredients";

    private static final String AUTH_HEADER = "Authorization";
    @Step("Send POST Request on create order without registration")
    public ValidatableResponse createOrder(Order body) {
        return given()
                .spec(getBaseSpec())
                .body(body)
                .when().log().everything()
                .post(ORDERS_PATH)
                .then().log().all();
    }

    @Step("Send POST Request on create order with registration")
    public ValidatableResponse createOrder(String token, Order body) {
        return given()
                .spec(getBaseSpec())
                .header(AUTH_HEADER, token)
                .body(body)
                .when().log().everything()
                .post(ORDERS_PATH)
                .then().log().all();
    }

    @Step("Send GET Request to get user orders")
    public ValidatableResponse getUserOrders(String token) {
        return given()
                .spec(getBaseSpec())
                .header(AUTH_HEADER, token)
                .when().log().everything()
                .get(ORDERS_PATH)
                .then().log().all();
    }

    @Step("Send GET Request to get ingredients")
    public IngredientsResponse getIngredients() {
        return given()
                .spec(getBaseSpec())
                .when().log().everything()
                .get(INGREDIENTS_PATH)
                .then().log().all()
                .extract().as(IngredientsResponse.class);
    }
}
