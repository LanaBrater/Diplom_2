import org.apache.commons.lang3.RandomStringUtils;
import pojo.Login;
import pojo.User;

public class RandomUserGenerator {
    private final String field = RandomStringUtils.randomAlphabetic(10);

    public User getRandomUser() {
        return new User(field + "@yandex.ru", field, field);
    }

    public User getRandomUserWithoutField(String name) {
        User user = new User();
        switch (name) {
            case "email":
                user.setPassword(field);
                user.setName(field);
                break;
            case "password":
                user.setEmail(field + "@yandex.ru");
                user.setName(field);
                break;
            case "name":
                user.setEmail(field + "@yandex.ru");
                user.setPassword(field);
                break;
        }
        return user;
    }

    public Login getLoginForm(User User) {
        return new Login(User.getEmail(), User.getPassword());
    }

    public Login getLoginFormWithIncorrectField(User User, String value) {
        Login loginForm = new Login();
        if (value.equals("email")){
            loginForm.setEmail(User.getEmail() + field);
            loginForm.setPassword(User.getPassword());
        } else if (value.equals("password")) {
            loginForm.setEmail(User.getEmail());
            loginForm.setPassword(User.getPassword() + field);
        }
        return loginForm;
    }
}
