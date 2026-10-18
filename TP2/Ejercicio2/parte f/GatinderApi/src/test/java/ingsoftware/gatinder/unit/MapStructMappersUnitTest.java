package ingsoftware.gatinder.unit;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ingsoftware.gatinder.dto.PetDto;
import ingsoftware.gatinder.dto.RegisterDto;
import ingsoftware.gatinder.dto.UserDto;
import ingsoftware.gatinder.dto.VoteDto;
import ingsoftware.gatinder.dto.ZoneDto;
import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.entity.Picture;
import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.entity.Vote;
import ingsoftware.gatinder.entity.Zone;
import ingsoftware.gatinder.enums.Animal;
import ingsoftware.gatinder.enums.Gender;
import ingsoftware.gatinder.mapper.PetMapper;
import ingsoftware.gatinder.mapper.UserMapper;
import ingsoftware.gatinder.mapper.VoteMapper;
import ingsoftware.gatinder.mapper.ZoneMapper;

import static org.junit.jupiter.api.Assertions.*;

public class MapStructMappersUnitTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final PetMapper petMapper = Mappers.getMapper(PetMapper.class);
    private final ZoneMapper zoneMapper = Mappers.getMapper(ZoneMapper.class);
    private final VoteMapper voteMapper = Mappers.getMapper(VoteMapper.class);

    @Test
    @DisplayName("MapStruct: UserMapper convierte User a UserDto correctamente")
    void testUserMapper_ToDto() {
        Zone zone = new Zone();
        zone.setId("zone-1");
        zone.setName("Palermo");

        Picture pic = new Picture();
        pic.setId("pic-1");

        User user = new User();
        user.setId("u-123");
        user.setFirstName("Juan");
        user.setLastName("Pérez");
        user.setEmail("juan@test.com");
        user.setZone(zone);
        user.setPicture(pic);
        user.setDeleted(false);

        UserDto dto = userMapper.toDto(user);

        assertNotNull(dto);
        assertEquals("u-123", dto.getId());
        assertEquals("Juan", dto.getFirstName());
        assertEquals("Pérez", dto.getLastName());
        assertEquals("juan@test.com", dto.getEmail());
        assertEquals("zone-1", dto.getZoneId());
        assertEquals("/api/pictures/user/u-123", dto.getPictureUrl());
        assertFalse(dto.isDeleted());
    }

    @Test
    @DisplayName("MapStruct: UserMapper convierte RegisterDto a User entity")
    void testUserMapper_ToEntity() {
        RegisterDto reg = new RegisterDto("Ana", "Gomez", "ana@test.com", "pass123", "pass123", "zone-1");
        User entity = userMapper.toEntity(reg);

        assertNotNull(entity);
        assertEquals("Ana", entity.getFirstName());
        assertEquals("Gomez", entity.getLastName());
        assertEquals("ana@test.com", entity.getEmail());
        assertEquals("pass123", entity.getPassword());
    }

    @Test
    @DisplayName("MapStruct: PetMapper convierte Pet a PetDto correctamente")
    void testPetMapper_ToDto() {
        User user = new User();
        user.setId("owner-99");

        Picture pic = new Picture();
        pic.setId("pic-pet");

        Pet pet = new Pet();
        pet.setId("pet-10");
        pet.setName("Pelusa");
        pet.setGender(Gender.FEMALE);
        pet.setAnimal(Animal.CAT);
        pet.setUser(user);
        pet.setPicture(pic);

        PetDto dto = petMapper.toDto(pet);

        assertNotNull(dto);
        assertEquals("pet-10", dto.getId());
        assertEquals("Pelusa", dto.getName());
        assertEquals(Gender.FEMALE, dto.getGender());
        assertEquals(Animal.CAT, dto.getAnimal());
        assertEquals("owner-99", dto.getUserId());
        assertEquals("/api/pictures/pet/pet-10", dto.getPictureUrl());
    }

    @Test
    @DisplayName("MapStruct: ZoneMapper convierte Zone a ZoneDto bidireccionalmente")
    void testZoneMapper() {
        Zone zone = new Zone();
        zone.setId("z-50");
        zone.setName("Almagro");
        zone.setDeleted(false);

        ZoneDto dto = zoneMapper.toDto(zone);
        assertEquals("z-50", dto.getId());
        assertEquals("Almagro", dto.getName());

        Zone entity = zoneMapper.toEntity(dto);
        assertEquals("z-50", entity.getId());
        assertEquals("Almagro", entity.getName());
    }

    @Test
    @DisplayName("MapStruct: VoteMapper mapea relaciones de mascotas a IDs y Nombres")
    void testVoteMapper_ToDto() {
        Pet p1 = new Pet();
        p1.setId("p1");
        p1.setName("Michi");

        Pet p2 = new Pet();
        p2.setId("p2");
        p2.setName("Firulais");

        Vote vote = new Vote();
        vote.setId("vote-1");
        vote.setDate(Instant.now());
        vote.setResponseDate(Instant.now());
        vote.setSenderPet(p1);
        vote.setReceiverPet(p2);

        VoteDto dto = voteMapper.toDto(vote);

        assertNotNull(dto);
        assertEquals("vote-1", dto.getId());
        assertEquals("p1", dto.getSenderPetId());
        assertEquals("Michi", dto.getSenderPetName());
        assertEquals("p2", dto.getReceiverPetId());
        assertEquals("Firulais", dto.getReceiverPetName());
    }
}
