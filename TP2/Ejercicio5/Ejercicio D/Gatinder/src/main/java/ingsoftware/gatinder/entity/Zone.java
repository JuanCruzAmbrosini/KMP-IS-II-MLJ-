package ingsoftware.gatinder.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @Entity
public class Zone {
    @Id private String id;
    private String name;
    private boolean deleted;
}
