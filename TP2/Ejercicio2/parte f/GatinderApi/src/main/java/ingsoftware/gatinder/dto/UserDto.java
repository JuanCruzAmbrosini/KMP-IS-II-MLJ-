package ingsoftware.gatinder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String zoneId;
    private String pictureUrl;
    private boolean deleted;
}
