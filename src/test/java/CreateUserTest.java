import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.User;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;

public class CreateUserTest {

    private UserAuth userAuth;
    private String token;

    @Before
    public void setUp() {
        userAuth = new UserAuth();
    }

    @After
    public void cleanUp() {
        if (token != null) {
            userAuth.deleteUser(token);
        }
    }

    @Test
    @Description("Создание уникального пользователя с валидными значениями")
    public void createUserWithValidValues() {
        User user= new RandomUserGenerator().getRandomUser();
        ValidatableResponse response = userAuth.createUser(user);
        checkResponseWithValidValues(response, user);
    }

    @Test
    @Description("Создание пользователя, который уже зарегистрирован")
    public void createExistingUser() {
        User createUserForm = new RandomUserGenerator().getRandomUser();
        userAuth.createUser(createUserForm);
        ValidatableResponse response = userAuth.createUser(createUserForm);
        checkResponseExistingUser(response);
    }

    @Test
    @Description("Создание пользователя без имени")
    public void createUserWithoutName() {
        ValidatableResponse response = userAuth.createUser(new RandomUserGenerator()
                .getRandomUserWithoutField("name"));
        checkResponseWithoutFields(response);
    }

    @Test
    @Description("Создание пользователя без Email")
    public void createUserWithoutEmail() {
        ValidatableResponse response = userAuth.createUser(new RandomUserGenerator()
                .getRandomUserWithoutField("email"));
        checkResponseWithoutFields(response);
    }

    @Test
    @Description("Создания пользователя без пароля")
    public void createUserWithoutPassword() {
        ValidatableResponse response = userAuth.createUser(new RandomUserGenerator()
                .getRandomUserWithoutField("password"));
        checkResponseWithoutFields(response);
    }

    @Step("Проверка ответа на создание нового пользователя")
    private void checkResponseWithValidValues(ValidatableResponse response, User user) {
        Boolean success = response.extract().path("success");
        token = response.extract().path("accessToken");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(user.getEmail().toLowerCase(), response.extract().path("user.email"));
        Assert.assertEquals(user.getName(), response.extract().path("user.name"));
        Assert.assertNotNull(token);
        Assert.assertNotNull(response.extract().path("refreshToken"));
    }

    @Step("Проверка ответа на созданее существующего пользователя")
    private void checkResponseExistingUser(ValidatableResponse response) {
        String expected = "User already exists";
        Boolean success = response.extract().path("success");
        Assert.assertEquals(SC_FORBIDDEN, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expected, response.extract().path("message"));
    }

    @Step("Проверка ответа метода")
    private void checkResponseWithoutFields(ValidatableResponse response) {
        String expected = "Email, password and name are required fields";
        Boolean success = response.extract().path("success");
        Assert.assertEquals(SC_FORBIDDEN, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expected, response.extract().path("message"));
    }
}
