import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.Changing;

import static org.apache.http.HttpStatus.*;

public class ChangeUserDataTest {
    private UserAuth userAuth;
    private String token;
    private final String email = RandomStringUtils.randomAlphabetic(10) + "@mail.com";
    private final String NAME = "NewTestName";

    @Before
    public void setUp() {
        userAuth = new UserAuth();
        ValidatableResponse response = userAuth.createUser(new RandomUserGenerator().getRandomUser());
        token = response.extract().path("accessToken");
    }

    @After
    public void cleanUp() {
        if (token != null) {
            userAuth.deleteUser(token);
        }
    }

    @Test
    @Description("Изменение данных пользователя с авторизацией")
    public void changeDataOfAuthUser() {
        ValidatableResponse response = userAuth.patchUserData(token, new Changing(email, NAME));
        checkResponseOfAuthUser(response);
    }

    @Test
    @Description("Изменение данных пользователя без авторизации")
    public void changeDataOfUnauthUser() {
        ValidatableResponse response = userAuth.patchUserData("default", new Changing(email, NAME));
        checkResponseOfUnauthUser(response);
    }

    @Test
    @Description("Изменение информации с почтой, которая уже используется")
    public void patchInfoByUserWithRepeatEmail() {
        String emailFail = "Test@yandex.ru";
        Changing body = new Changing(emailFail, NAME);
        ValidatableResponse response = userAuth.patchUserData(token, body);
        checkResponseWithExistingEmail(response);
    }

    @Step("Проверка ответа на изменение пользователя с существующей почтой")
    private void checkResponseWithExistingEmail(ValidatableResponse response) {
        String expectedMessage = "User with such email already exists";
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_FORBIDDEN, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expectedMessage, response.extract().path("message"));
    }

    @Step("Проверка ответа на изменение авторизованного пользователя")
    private void checkResponseOfAuthUser(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(email.toLowerCase(), response.extract().path("user.email"));
        Assert.assertEquals(NAME, response.extract().path("user.name"));
    }

    @Step("Проверка ответа на изменение неавторизованного пользователя")
    private void checkResponseOfUnauthUser(ValidatableResponse response) {
        String expectedMessage = "You should be authorised";
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_UNAUTHORIZED, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expectedMessage, response.extract().path("message"));
    }
}
