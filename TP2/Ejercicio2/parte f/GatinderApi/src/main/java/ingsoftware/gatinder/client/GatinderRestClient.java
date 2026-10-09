package ingsoftware.gatinder.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ingsoftware.gatinder.dto.*;

@Service
public class GatinderRestClient {

    @Autowired
    private RestTemplate restTemplate;

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    // --- USUARIOS ---
    public List<UserDto> getUsers(String baseUrl) {
        UserDto[] users = restTemplate.getForObject(baseUrl + "/api/users", UserDto[].class);
        return users == null ? Collections.emptyList() : Arrays.asList(users);
    }

    public UserDto getUserById(String baseUrl, String id) {
        return restTemplate.getForObject(baseUrl + "/api/users/" + id, UserDto.class);
    }

    public UserDto registerUser(String baseUrl, RegisterDto dto) {
        return restTemplate.postForObject(baseUrl + "/api/auth/register", dto, UserDto.class);
    }

    public AuthenticatedUserDto login(String baseUrl, LoginDto dto) {
        return restTemplate.postForObject(baseUrl + "/api/auth/login", dto, AuthenticatedUserDto.class);
    }

    // --- MASCOTAS ---
    public List<PetDto> getPets(String baseUrl) {
        PetDto[] pets = restTemplate.getForObject(baseUrl + "/api/pets", PetDto[].class);
        return pets == null ? Collections.emptyList() : Arrays.asList(pets);
    }

    public PetDto getPetById(String baseUrl, String id) {
        return restTemplate.getForObject(baseUrl + "/api/pets/" + id, PetDto.class);
    }

    public List<PetDto> getPetsByUser(String baseUrl, String userId) {
        PetDto[] pets = restTemplate.getForObject(baseUrl + "/api/pets/user/" + userId, PetDto[].class);
        return pets == null ? Collections.emptyList() : Arrays.asList(pets);
    }

    public PetDto createPet(String baseUrl, PetDto petDto) {
        return restTemplate.postForObject(baseUrl + "/api/pets", petDto, PetDto.class);
    }

    public PetDto updatePet(String baseUrl, String id, PetDto petDto) {
        HttpEntity<PetDto> requestEntity = new HttpEntity<>(petDto);
        ResponseEntity<PetDto> response = restTemplate.exchange(
                baseUrl + "/api/pets/" + id,
                HttpMethod.PUT,
                requestEntity,
                PetDto.class
        );
        return response.getBody();
    }

    public void deletePet(String baseUrl, String id, String userId) {
        restTemplate.delete(baseUrl + "/api/pets/" + id + "?userId=" + userId);
    }

    // --- ZONAS ---
    public List<ZoneDto> getZones(String baseUrl) {
        ZoneDto[] zones = restTemplate.getForObject(baseUrl + "/api/zones", ZoneDto[].class);
        return zones == null ? Collections.emptyList() : Arrays.asList(zones);
    }

    public ZoneDto getZoneById(String baseUrl, String id) {
        return restTemplate.getForObject(baseUrl + "/api/zones/" + id, ZoneDto.class);
    }

    public ZoneDto createZone(String baseUrl, ZoneDto zoneDto) {
        return restTemplate.postForObject(baseUrl + "/api/zones", zoneDto, ZoneDto.class);
    }

    // --- VOTOS ---
    public List<VoteDto> getVotes(String baseUrl) {
        VoteDto[] votes = restTemplate.getForObject(baseUrl + "/api/votes", VoteDto[].class);
        return votes == null ? Collections.emptyList() : Arrays.asList(votes);
    }

    public VoteDto submitVote(String baseUrl, String userId, VoteRequestDto request) {
        return restTemplate.postForObject(baseUrl + "/api/votes?userId=" + userId, request, VoteDto.class);
    }

    public VoteDto respondVote(String baseUrl, String userId, String voteId) {
        HttpEntity<?> requestEntity = HttpEntity.EMPTY;
        ResponseEntity<VoteDto> response = restTemplate.exchange(
                baseUrl + "/api/votes/" + voteId + "/respond?userId=" + userId,
                HttpMethod.PUT,
                requestEntity,
                VoteDto.class
        );
        return response.getBody();
    }

    public List<VoteReportDto> getVoteReport(String baseUrl) {
        VoteReportDto[] report = restTemplate.getForObject(baseUrl + "/api/votes/report", VoteReportDto[].class);
        return report == null ? Collections.emptyList() : Arrays.asList(report);
    }

    // --- INTEGRACIÓN EXTERNA (API Externa con RestTemplate y fallback) ---
    public ExternalFactDto getRandomPetFact() {
        try {
            return restTemplate.getForObject("https://catfact.ninja/fact", ExternalFactDto.class);
        } catch (Exception e) {
            // Fallback elegante en caso de desconexión externa
            return new ExternalFactDto("Los gatos tienen 32 músculos en cada oreja y duermen el 70% de sus vidas.", 72);
        }
    }
}
