package pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ingredient {

    @JsonProperty("_id")
    public String id;
    @JsonProperty("name")
    public String name;
    @JsonProperty("type")
    public String type;
    @JsonProperty("proteins")
    public Integer proteins;
    @JsonProperty("fat")
    public Integer fat;
    @JsonProperty("carbohydrates")
    public Integer carbohydrates;
    @JsonProperty("calories")
    public Integer calories;
    @JsonProperty("price")
    public Integer price;
    @JsonProperty("image")
    public String image;
    @JsonProperty("image_mobile")
    public String imageMobile;
    @JsonProperty("image_large")
    public String imageLarge;
    @JsonProperty("__v")
    public Integer v;
}