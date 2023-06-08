import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import pojo.Changing;
import pojo.Login;
import pojo.User;

import static io.restassured.RestAssured.given;

public class UserAuth extends StellarBurgerMainPage {
    private static final String AUTH_PATH = "/api/auth";

    @Step("Send POST Request")
    public ValidatableResponse createUser(User body) {
        return given()
                .spec(getBaseSpec())
                .body(body)
                .when().log().everything()
                .post(AUTH_PATH + "/register")
                .then().log().all();
    }

    @Step("Send POST Request")
    public ValidatableResponse loginUser(Login body) {
        return given()
                .spec(getBaseSpec())
                .body(body)
                .when().log().everything()
                .post(AUTH_PATH + "/login")
                .then().log().all();
    }

    @Step("Send GET Request")
    public ValidatableResponse getUserInfo(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when().log().everything()
                .get(AUTH_PATH + "/user")
                .then().log().all();
    }

    @Step("Send PATCH Request")
    public ValidatableResponse patchUserData(String token, Changing body) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .body(body)
                .when().log().everything()
                .patch(AUTH_PATH + "/user")
                .then().log().all();
    }

    @Step("Send DELETE Request")
    public ValidatableResponse deleteUser(String token) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", token)
                .when().log().everything()
                .delete(AUTH_PATH + "/user")
                .then().log().all();
    }
}
