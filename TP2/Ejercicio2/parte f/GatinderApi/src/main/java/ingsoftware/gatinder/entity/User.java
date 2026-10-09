package ingsoftware.gatinder.entity;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @Entity
public class User {
    @Id private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @ManyToOne private Zone zone;
    @OneToOne private Picture picture;
    private Instant createdAt;
    private Instant deletedAt;
    private boolean deleted;
    private String rememberToken;
    private Instant rememberTokenExpiresAt;
}
