import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pojo.IngredientsResponse;
import pojo.Order;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class GetUserOrdersTest {
    private UserOrders userOrders;
    private String token;
    private String expectedId;
    private String expectedStatus;
    private String expectedName;
    private String expectedCreatedAt;
    private String expectedUpdatedAt;
    private String expectedNumber;

    @Before
    public void setUp() {
        userOrders = new UserOrders();
        token = new UserAuth().createUser(new RandomUserGenerator().getRandomUser())
                .extract()
                .path("accessToken");
        IngredientsResponse ingredientsResponse = userOrders.getIngredients();
        String[] ingredients = new String[] {ingredientsResponse.getData().get(0).getId(), ingredientsResponse.getData().get(1).getId()};
        ValidatableResponse response = userOrders.createOrder(token, new Order(ingredients));
        expectedId = response.extract().path("order._id");
        expectedStatus = response.extract().path("order.status");
        expectedName = response.extract().path("order.name");
        expectedCreatedAt = response.extract().path("order.createdAt");
        expectedUpdatedAt = response.extract().path("order.updatedAt");
        expectedNumber = response.extract().path("order.number").toString();
    }

    @After
    public void cleanUp() {
        if (token != null) {
            new UserAuth().deleteUser(token);
        }
    }

    @Test
    @Description("Получение заказов авторизованного пользователя")
    public void getOrderOfAuthUser() {
        ValidatableResponse response = userOrders.getUserOrders(token);
        checkResponseOfAuthUser(response);
    }

    @Test
    @Description("Получение заказов неавторизованного пользвателя")
    public void getOrderOfUnauthUser() {
        ValidatableResponse response = userOrders.getUserOrders("default");
        checkResponseOfUnauthUser(response);
    }

    @Step("Проверка ответа на получение заказов авторизованного пользователя")
    private void checkResponseOfAuthUser(ValidatableResponse response) {
        boolean success = response.extract().path("success");
        String actualId = response.extract().path("orders._id").toString();
        String actualStatus = response.extract().path("orders.status").toString();
        String actualName = response.extract().path("orders.name").toString();
        String actualCreated = response.extract().path("orders.createdAt").toString();
        String actualUpdated = response.extract().path("orders.updatedAt").toString();
        String actualNumber = response.extract().path("orders.number").toString();

        Assert.assertEquals(SC_OK, response.extract().statusCode());
        Assert.assertEquals(Boolean.TRUE, success);
        Assert.assertEquals(expectedId, actualId.substring(1, actualId.length() - 1));
        Assert.assertEquals(expectedStatus, actualStatus.substring(1, actualStatus.length() - 1));
        Assert.assertEquals(expectedName, actualName.substring(1, actualName.length() - 1));
        Assert.assertEquals(expectedCreatedAt, actualCreated.substring(1, actualCreated.length() - 1));
        Assert.assertEquals(expectedUpdatedAt, actualUpdated.substring(1, actualUpdated.length() - 1));
        Assert.assertEquals(expectedNumber, actualNumber.substring(1, actualNumber.length() - 1));
    }

    @Step("Проверка ответа на получение заказов неавторизованного пользователя")
    private void checkResponseOfUnauthUser(ValidatableResponse response) {
        String expectedMessage = "You should be authorised";
        Assert.assertEquals(SC_UNAUTHORIZED, response.extract().statusCode());
        Assert.assertEquals(Boolean.FALSE, response.extract().path("success"));
        Assert.assertEquals(expectedMessage, response.extract().path("message"));
    }
}
