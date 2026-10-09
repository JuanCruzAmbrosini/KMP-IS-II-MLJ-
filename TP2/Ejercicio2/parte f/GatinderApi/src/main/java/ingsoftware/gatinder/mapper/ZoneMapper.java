package ingsoftware.gatinder.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import ingsoftware.gatinder.dto.ZoneDto;
import ingsoftware.gatinder.entity.Zone;

@Mapper(componentModel = "spring")
public interface ZoneMapper {

    ZoneDto toDto(Zone zone);

    List<ZoneDto> toDtoList(List<Zone> zones);

    Zone toEntity(ZoneDto dto);
}
