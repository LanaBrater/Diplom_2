import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.IngredientsResponse;
import pojo.Order;
import io.qameta.allure.Step;
import pojo.User;

import static org.apache.http.HttpStatus.*;

public class CreateOrderTest {

    private UserOrders userOrders;
    private String token;
    private final String BURGER_NAME = "Бессмертный флюоресцентный бургер";
    private User user;

    private ValidatableResponse validatableResponse;
    @Before
    public void setUp() {
        userOrders = new UserOrders();
        user = new RandomUserGenerator().getRandomUser();
        validatableResponse = new UserAuth().createUser(user);
        token = validatableResponse
                .extract()
                .path("accessToken");
    }

    @After
    public void cleanUp() {
        if (token != null) {
            new UserAuth().deleteUser(token);
        }
    }

    @Test
    @Description("Создание заказа с авторизацией")
    public void createOrderWithAuth() {
        new UserAuth().loginUser(new RandomUserGenerator().getLoginForm(user));
        IngredientsResponse ingredientsResponse = userOrders.getIngredients();
        String[] ingredients = new String[] {ingredientsResponse.getData().get(0).getId(), ingredientsResponse.getData().get(1).getId()};
        ValidatableResponse response = userOrders.createOrder(token, new Order(ingredients));
        checkResponseWithAuth(response);
    }

    @Test
    @Description("Создание заказа без авторизации")
    public void createOrderWithoutAuth() {
        IngredientsResponse ingredientsResponse = userOrders.getIngredients();
        String[] ingredients = new String[] {ingredientsResponse.getData().get(0).getId(), ingredientsResponse.getData().get(1).getId()};
        ValidatableResponse response = userOrders.createOrder(new Order(ingredients));
        checkResponseWithoutAuth(response);
    }

    @Test
    @Description("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        ValidatableResponse response = userOrders.createOrder(new Order(null));
        checkResponseWithoutIngredients(response);
    }

    @Test
    @Description("Создание заказа c неверным хешем ингредиентов")
    public void createOrderWithIncorrectHash() {
        ValidatableResponse response = userOrders.createOrder(new Order(new String[]{"123"}));
        checkResponseWithIncorrectHash(response);
    }

    @Step("Проверка ответа на создание заказа с авторизацией")
    private void checkResponseWithAuth(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        ValidatableResponse getUserInfo = new UserAuth().getUserInfo(token);
        String ownerName = getUserInfo.extract().path("user.name");
        String ownerEmail = getUserInfo.extract().path("user.email");

        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(BURGER_NAME, response.extract().path("name"));
        Assert.assertNotNull(response.extract().path("order.number"));
        Assert.assertEquals(ownerName, response.extract().path("order.owner.name"));
        Assert.assertEquals(ownerEmail, response.extract().path("order.owner.email"));
        Assert.assertNotNull(response.extract().path("order.owner.createdAt"));
        Assert.assertNotNull(response.extract().path("order.owner.updatedAt"));
        Assert.assertNotNull(response.extract().path("order.status"));
        Assert.assertEquals(BURGER_NAME, response.extract().path("order.name"));
        Assert.assertNotNull(response.extract().path("order.createdAt"));
        Assert.assertNotNull(response.extract().path("order.createdAt"));
        Assert.assertNotNull(response.extract().path("order.number"));
        Assert.assertNotNull(response.extract().path("order.price"));
    }

    @Step("Проверка ответа на создание заказа без авторизации")
    private void checkResponseWithoutAuth(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(BURGER_NAME, response.extract().path("name"));
        Assert.assertNotNull(response.extract().path("order.number"));
    }

    @Step("Проверка ответа на создание заказа без ингредиентов")
    private void checkResponseWithoutIngredients(ValidatableResponse response) {
        String expectedMessage = "Ingredient ids must be provided";
        boolean success = response.extract().path("success");
        Assert.assertEquals(SC_BAD_REQUEST, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, success);
        Assert.assertEquals(expectedMessage, response.extract().path("message"));
    }

    @Step("Проверка ответа на создание заказа с неверным хешем ингредиентов")
    private void checkResponseWithIncorrectHash(ValidatableResponse response) {
        Assert.assertEquals(SC_INTERNAL_SERVER_ERROR, response.extract().statusCode());
    }
}
