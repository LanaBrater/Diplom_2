package pojo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public class Order {

    private String[] ingredients;

    @Override
    public String toString() {
        return "ingredients: " + Arrays.toString(ingredients);
    }
}
