package ingsoftware.gatinder.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import ingsoftware.gatinder.dto.LoginDto;
import ingsoftware.gatinder.dto.PetDto;
import ingsoftware.gatinder.dto.RegisterDto;
import ingsoftware.gatinder.dto.VoteRequestDto;
import ingsoftware.gatinder.dto.ZoneDto;
import ingsoftware.gatinder.enums.Animal;
import ingsoftware.gatinder.enums.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class GatinderRestControllersIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired(required = false)
    private ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("REST API: Flujo completo CRUD de Zonas")
    void testZoneRestEndpoints() throws Exception {
        // 1. Crear Zona POST /api/zones
        ZoneDto newZone = new ZoneDto(null, "Caballito", false);
        MvcResult postRes = mockMvc.perform(post("/api/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newZone)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Caballito"))
                .andReturn();

        ZoneDto createdZone = objectMapper.readValue(postRes.getResponse().getContentAsString(), ZoneDto.class);
        String zoneId = createdZone.getId();

        // 2. Obtener Zona GET /api/zones/{id}
        mockMvc.perform(get("/api/zones/" + zoneId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Caballito"));

        // 3. Modificar Zona PUT /api/zones/{id}
        ZoneDto updateZone = new ZoneDto(null, "Caballito Norte", false);
        mockMvc.perform(put("/api/zones/" + zoneId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateZone)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Caballito Norte"));

        // 4. Listar Zonas GET /api/zones
        mockMvc.perform(get("/api/zones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("REST API: Registro, Autenticación y Consulta de Usuarios")
    void testUserAndAuthRestEndpoints() throws Exception {
        // Crear Zona previa
        ZoneDto zone = new ZoneDto(null, "Belgrano R", false);
        MvcResult zRes = mockMvc.perform(post("/api/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zone)))
                .andExpect(status().isCreated())
                .andReturn();
        ZoneDto createdZone = objectMapper.readValue(zRes.getResponse().getContentAsString(), ZoneDto.class);

        // 1. Registro POST /api/auth/register
        RegisterDto regDto = new RegisterDto("Carlos", "Sanchez", "carlos@rest.com", "pass123", "pass123", createdZone.getId());
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("carlos@rest.com"))
                .andExpect(jsonPath("$.firstName").value("Carlos"));

        // 2. Login POST /api/auth/login
        LoginDto loginDto = new LoginDto("carlos@rest.com", "pass123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("carlos@rest.com"))
                .andExpect(jsonPath("$.rememberToken").isNotEmpty());

        // 3. Listar usuarios GET /api/users
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("REST API: Flujo completo de Mascotas y Votos")
    void testPetsAndVotesRestEndpoints() throws Exception {
        // 1. Crear zona y dos usuarios
        ZoneDto zone = new ZoneDto(null, "Nunez", false);
        MvcResult zRes = mockMvc.perform(post("/api/zones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(zone)))
                .andExpect(status().isCreated())
                .andReturn();
        String zoneId = objectMapper.readValue(zRes.getResponse().getContentAsString(), ZoneDto.class).getId();

        RegisterDto u1Reg = new RegisterDto("User1", "Test", "u1@pets.com", "123456", "123456", zoneId);
        MvcResult u1Res = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u1Reg)))
                .andExpect(status().isCreated())
                .andReturn();
        String u1Id = objectMapper.readTree(u1Res.getResponse().getContentAsString()).get("id").asText();

        RegisterDto u2Reg = new RegisterDto("User2", "Test", "u2@pets.com", "123456", "123456", zoneId);
        MvcResult u2Res = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(u2Reg)))
                .andExpect(status().isCreated())
                .andReturn();
        String u2Id = objectMapper.readTree(u2Res.getResponse().getContentAsString()).get("id").asText();

        // 2. Crear mascota 1 POST /api/pets
        PetDto p1Dto = new PetDto(null, "Tom", Gender.MALE, Animal.CAT, u1Id, null);
        MvcResult p1Res = mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p1Dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tom"))
                .andReturn();
        String p1Id = objectMapper.readTree(p1Res.getResponse().getContentAsString()).get("id").asText();

        // 3. Crear mascota 2 POST /api/pets
        PetDto p2Dto = new PetDto(null, "Jerry", Gender.MALE, Animal.CAT, u2Id, null);
        MvcResult p2Res = mockMvc.perform(post("/api/pets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p2Dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jerry"))
                .andReturn();
        String p2Id = objectMapper.readTree(p2Res.getResponse().getContentAsString()).get("id").asText();

        // 4. Emitir voto POST /api/votes
        VoteRequestDto voteReq = new VoteRequestDto(p1Id, p2Id);
        MvcResult voteRes = mockMvc.perform(post("/api/votes?userId=" + u1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.senderPetId").value(p1Id))
                .andExpect(jsonPath("$.receiverPetId").value(p2Id))
                .andReturn();
        String voteId = objectMapper.readTree(voteRes.getResponse().getContentAsString()).get("id").asText();

        // 5. Responder voto PUT /api/votes/{id}/respond
        mockMvc.perform(put("/api/votes/" + voteId + "/respond?userId=" + u2Id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseDate").isNotEmpty());

        // 6. Consultar reporte de votos GET /api/votes/report
        mockMvc.perform(get("/api/votes/report"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
