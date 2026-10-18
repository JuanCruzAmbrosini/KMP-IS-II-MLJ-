package ingsoftware.gatinder.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteDto {
    private String id;
    private Instant date;
    private Instant responseDate;
    private String senderPetId;
    private String senderPetName;
    private String receiverPetId;
    private String receiverPetName;
}
