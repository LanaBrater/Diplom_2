import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.User;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class LoginUserTest {

    private UserAuth userAuth;
    private User user;
    private String token;

    @Before
    public void setUp() {
        userAuth = new UserAuth();
        user = new RandomUserGenerator().getRandomUser();
        userAuth.createUser(user);
    }

    @After
    public void cleanUp() {
        if (token != null) {
            userAuth.deleteUser(token);
        }
    }

    @Test
    @Description("Логин под существующим пользователем")
    public void loginExistingUser() {
        ValidatableResponse response = userAuth.loginUser(new RandomUserGenerator().getLoginForm(user));
        checkResponseWithCorrectFields(response);
    }

    @Test
    @Description("Логин с некорректным Email")
    public void loginWithIncorrectEmail() {
        ValidatableResponse response = userAuth.loginUser(
                new RandomUserGenerator().getLoginFormWithIncorrectField(user,"email"));
        checkResponseWithIncorrectFields(response);
    }

    @Test
    @Description("Логин с неккоректным паролем")
    public void loginWithIncorrectPassword() {
        ValidatableResponse response = userAuth.loginUser(
                new RandomUserGenerator().getLoginFormWithIncorrectField(user,"password"));
        checkResponseWithIncorrectFields(response);
    }

    @Test
    @Description("Логин с неккоректным Email и паролем")
    public void loginWithIncorrectFields() {
        ValidatableResponse response = userAuth.loginUser(
                new RandomUserGenerator().getLoginForm(new RandomUserGenerator().getRandomUser()));
        checkResponseWithIncorrectFields(response);
    }

    @Step("Проверка логина с корректными значениями")
    private void checkResponseWithCorrectFields(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        token = response.extract().path("accessToken");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertNotNull(token);
        Assert.assertNotNull(response.extract().path("refreshToken"));
        Assert.assertEquals(user.getEmail().toLowerCase(), response.extract().path("user.email"));
        Assert.assertEquals(user.getName(), response.extract().path("user.name"));
    }

    @Step("Проверка логина с некорректными значениями")
    private void checkResponseWithIncorrectFields(ValidatableResponse response) {
        String expected = "email or password are incorrect";
        Boolean success = response.extract().path("success");
        Assert.assertEquals(SC_UNAUTHORIZED, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expected, response.extract().path("message"));
    }
}
