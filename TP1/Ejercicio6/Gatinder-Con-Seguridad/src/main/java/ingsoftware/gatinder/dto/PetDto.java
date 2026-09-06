package ingsoftware.gatinder.dto;

import ingsoftware.gatinder.enums.Animal;
import ingsoftware.gatinder.enums.Gender;

public class PetDto {
    private String id;
    private String name;
    private Gender gender;
    private Animal animal;
    private String userId;
    private String pictureUrl;

    public PetDto() {}

    public PetDto(String id, String name, Gender gender, Animal animal, String userId, String pictureUrl) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.animal = animal;
        this.userId = userId;
        this.pictureUrl = pictureUrl;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Gender getGender() { return gender; }
    public Animal getAnimal() { return animal; }
    public String getUserId() { return userId; }
    public String getPictureUrl() { return pictureUrl; }
    public String getPicture() { return pictureUrl; }
}
