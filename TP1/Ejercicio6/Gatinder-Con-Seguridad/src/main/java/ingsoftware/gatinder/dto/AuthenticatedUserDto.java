package ingsoftware.gatinder.dto;

public class AuthenticatedUserDto {
    private final UserDto user;
    private final String rememberToken;

    public AuthenticatedUserDto(UserDto user, String rememberToken) {
        this.user = user;
        this.rememberToken = rememberToken;
    }

    public UserDto getUser() { return user; }
    public String getRememberToken() { return rememberToken; }
}
