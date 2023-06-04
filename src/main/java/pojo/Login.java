package pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Login {
    private String email;
    private String password;

    public Login() {
    }

    @Override
    public String toString() {
        return "email: " + email + ", password: " + password;
    }
}
