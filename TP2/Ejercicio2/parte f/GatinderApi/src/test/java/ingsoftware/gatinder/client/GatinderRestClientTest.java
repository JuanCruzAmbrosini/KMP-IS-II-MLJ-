package ingsoftware.gatinder.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import ingsoftware.gatinder.dto.ExternalFactDto;
import ingsoftware.gatinder.dto.PetDto;
import ingsoftware.gatinder.dto.UserDto;
import ingsoftware.gatinder.dto.VoteDto;
import ingsoftware.gatinder.dto.VoteRequestDto;
import ingsoftware.gatinder.dto.ZoneDto;
import ingsoftware.gatinder.enums.Animal;
import ingsoftware.gatinder.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest
public class GatinderRestClientTest {

    @Autowired
    private GatinderRestClient client;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired(required = false)
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private MockRestServiceServer mockServer;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
    }

    @Test
    @DisplayName("RestTemplate Client: Obtener lista de usuarios mediante GET /api/users")
    void testGetUsers() throws Exception {
        UserDto u = new UserDto("u1", "Mateo", "Peralta", "mateo@test.com", "z1", null, false);
        mockServer.expect(requestTo(baseUrl + "/api/users"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(List.of(u)), MediaType.APPLICATION_JSON));

        List<UserDto> users = client.getUsers(baseUrl);

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("Mateo", users.get(0).getFirstName());
        mockServer.verify();
    }

    @Test
    @DisplayName("RestTemplate Client: Obtener mascotas mediante GET /api/pets")
    void testGetPets() throws Exception {
        PetDto p = new PetDto("p1", "Michi", Gender.MALE, Animal.CAT, "u1", "/api/pictures/pet/p1");
        mockServer.expect(requestTo(baseUrl + "/api/pets"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(List.of(p)), MediaType.APPLICATION_JSON));

        List<PetDto> pets = client.getPets(baseUrl);

        assertNotNull(pets);
        assertEquals(1, pets.size());
        assertEquals("Michi", pets.get(0).getName());
        mockServer.verify();
    }

    @Test
    @DisplayName("RestTemplate Client: Crear zona mediante POST /api/zones")
    void testCreateZone() throws Exception {
        ZoneDto input = new ZoneDto(null, "Colegiales", false);
        ZoneDto output = new ZoneDto("z-99", "Colegiales", false);

        mockServer.expect(requestTo(baseUrl + "/api/zones"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(output), MediaType.APPLICATION_JSON));

        ZoneDto result = client.createZone(baseUrl, input);

        assertNotNull(result);
        assertEquals("z-99", result.getId());
        assertEquals("Colegiales", result.getName());
        mockServer.verify();
    }

    @Test
    @DisplayName("RestTemplate Client: Emitir voto mediante POST /api/votes")
    void testSubmitVote() throws Exception {
        VoteRequestDto req = new VoteRequestDto("p1", "p2");
        VoteDto res = new VoteDto("v-1", Instant.now(), null, "p1", "Michi", "p2", "Pelusa");

        mockServer.expect(requestTo(baseUrl + "/api/votes?userId=u1"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(res), MediaType.APPLICATION_JSON));

        VoteDto result = client.submitVote(baseUrl, "u1", req);

        assertNotNull(result);
        assertEquals("v-1", result.getId());
        assertEquals("Michi", result.getSenderPetName());
        mockServer.verify();
    }

    @Test
    @DisplayName("RestTemplate Client: Consumo de API externa de hechos de mascotas con soporte de fallback")
    void testExternalApiFact() throws Exception {
        ExternalFactDto mockFact = new ExternalFactDto("Los gatos tienen 32 musculos en cada oreja.", 44);
        mockServer.expect(requestTo("https://catfact.ninja/fact"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(mockFact), MediaType.APPLICATION_JSON));

        ExternalFactDto fact = client.getRandomPetFact();
        assertNotNull(fact);
        assertEquals("Los gatos tienen 32 musculos en cada oreja.", fact.getFact());
        mockServer.verify();
    }

    @Test
    @DisplayName("RestTemplate Client: Fallback de API externa ante error 500")
    void testExternalApiFactFallback() {
        mockServer.expect(requestTo("https://catfact.ninja/fact"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        ExternalFactDto fact = client.getRandomPetFact();
        assertNotNull(fact);
        assertTrue(fact.getFact().contains("gatos"));
        mockServer.verify();
    }
}
