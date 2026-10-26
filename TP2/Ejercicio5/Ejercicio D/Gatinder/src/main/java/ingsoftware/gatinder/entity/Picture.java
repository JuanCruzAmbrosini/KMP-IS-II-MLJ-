package ingsoftware.gatinder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data 
@NoArgsConstructor 
@Entity
public class Picture {
    @Id 
    private String id;

    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(columnDefinition = "BLOB")
    private byte[] data;

    private String mime;
    private boolean deleted;
}
