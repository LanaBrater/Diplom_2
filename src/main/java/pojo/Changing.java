package pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Changing {
    private String email;
    private String name;

    @Override
    public String toString() {
        return "email: " + email + ", name: " + name;
    }
}
