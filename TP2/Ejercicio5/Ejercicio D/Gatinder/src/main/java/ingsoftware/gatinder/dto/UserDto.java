package ingsoftware.gatinder.dto;

public class UserDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String zoneId;
    private String pictureUrl;
    private boolean deleted;

    public UserDto() {}

    public UserDto(String id, String firstName, String lastName, String email, String zoneId, String pictureUrl, boolean deleted) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.zoneId = zoneId;
        this.pictureUrl = pictureUrl;
        this.deleted = deleted;
    }

    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getZoneId() { return zoneId; }
    public String getPictureUrl() { return pictureUrl; }
    public boolean isDeleted() { return deleted; }
}
