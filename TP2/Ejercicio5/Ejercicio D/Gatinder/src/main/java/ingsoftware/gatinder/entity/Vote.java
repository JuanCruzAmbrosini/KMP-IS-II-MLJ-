package ingsoftware.gatinder.entity;

import java.time.Instant;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @Entity
public class Vote {
    @Id private String id;
    private Instant date;
    private Instant responseDate;
    @ManyToOne private Pet senderPet;
    @ManyToOne private Pet receiverPet;
}
