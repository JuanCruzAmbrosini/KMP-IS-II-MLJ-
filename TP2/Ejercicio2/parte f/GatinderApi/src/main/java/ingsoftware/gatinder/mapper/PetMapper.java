package ingsoftware.gatinder.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ingsoftware.gatinder.dto.PetDto;
import ingsoftware.gatinder.entity.Pet;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "pictureUrl", source = "pet", qualifiedByName = "petToPictureUrl")
    PetDto toDto(Pet pet);

    List<PetDto> toDtoList(List<Pet> pets);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "picture", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Pet toEntity(PetDto dto);

    @Named("petToPictureUrl")
    default String petToPictureUrl(Pet pet) {
        if (pet == null || pet.getPicture() == null || pet.getId() == null) {
            return null;
        }
        return "/api/pictures/pet/" + pet.getId();
    }
}
