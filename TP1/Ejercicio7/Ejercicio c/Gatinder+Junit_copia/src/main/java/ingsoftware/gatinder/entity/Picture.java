package ingsoftware.gatinder.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @Entity
public class Picture {
    @Id private String id;
    @Lob @Basic(fetch = FetchType.LAZY) private byte[] data;
    private String mime;
    private boolean deleted;
}
