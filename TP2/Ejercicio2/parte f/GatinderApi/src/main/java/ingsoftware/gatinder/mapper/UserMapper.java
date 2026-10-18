package ingsoftware.gatinder.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ingsoftware.gatinder.dto.RegisterDto;
import ingsoftware.gatinder.dto.UserDto;
import ingsoftware.gatinder.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "zoneId", source = "zone.id")
    @Mapping(target = "pictureUrl", source = "user", qualifiedByName = "userToPictureUrl")
    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "zone", ignore = true)
    @Mapping(target = "picture", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "rememberToken", ignore = true)
    @Mapping(target = "rememberTokenExpiresAt", ignore = true)
    User toEntity(RegisterDto dto);

    @Named("userToPictureUrl")
    default String userToPictureUrl(User user) {
        if (user == null || user.getPicture() == null || user.getId() == null) {
            return null;
        }
        return "/api/pictures/user/" + user.getId();
    }
}
