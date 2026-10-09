package ingsoftware.gatinder.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ingsoftware.gatinder.dto.VoteDto;
import ingsoftware.gatinder.entity.Vote;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    @Mapping(target = "senderPetId", source = "senderPet.id")
    @Mapping(target = "senderPetName", source = "senderPet.name")
    @Mapping(target = "receiverPetId", source = "receiverPet.id")
    @Mapping(target = "receiverPetName", source = "receiverPet.name")
    VoteDto toDto(Vote vote);

    List<VoteDto> toDtoList(List<Vote> votes);
}
