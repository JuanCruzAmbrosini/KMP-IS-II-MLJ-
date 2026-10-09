package ingsoftware.gatinder.dto;

import ingsoftware.gatinder.enums.Animal;
import ingsoftware.gatinder.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetDto {
    private String id;
    private String name;
    private Gender gender;
    private Animal animal;
    private String userId;
    private String pictureUrl;

    public String getPicture() {
        return pictureUrl;
    }
}
