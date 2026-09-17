package ingsoftware.gatinder.entity;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import ingsoftware.gatinder.enums.Gender;
import ingsoftware.gatinder.enums.Animal;

@Data @NoArgsConstructor @Entity
public class Pet {
    @Id private String id;
    private String name;
    @Enumerated(EnumType.STRING) private Gender gender;
    @Enumerated(EnumType.STRING) private Animal animal;
    @ManyToOne private User user;
    @OneToOne private Picture picture;
    private Instant createdAt;
    private Instant deletedAt;
    private boolean deleted;
}
