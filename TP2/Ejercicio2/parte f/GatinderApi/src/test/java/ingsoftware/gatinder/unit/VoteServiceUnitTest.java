package ingsoftware.gatinder.unit;

import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.entity.User;
import ingsoftware.gatinder.entity.Vote;
import ingsoftware.gatinder.repository.VoteRepository;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.PetService;
import ingsoftware.gatinder.service.VoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TIPO DE PRUEBA: PRUEBA UNITARIA (UNIT TESTING)
 * Objetivo: Validar de forma aislada las reglas de votación y match entre mascotas
 * controlando posibles condiciones de error (auto-voto, suplantación de identidad).
 */
@ExtendWith(MockitoExtension.class)
public class VoteServiceUnitTest {

    @Mock
    private PetService petService;

    @Mock
    private VoteRepository voteRepository;

    @InjectMocks
    private VoteService voteService;

    private User user1;
    private User user2;
    private Pet senderPet;
    private Pet receiverPet;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId("user-1");

        user2 = new User();
        user2.setId("user-2");

        senderPet = new Pet();
        senderPet.setId("pet-1");
        senderPet.setUser(user1);

        receiverPet = new Pet();
        receiverPet.setId("pet-2");
        receiverPet.setUser(user2);
    }

    @Test
    @DisplayName("Unit Test: Voto valido entre dos mascotas diferentes se registra correctamente")
    void testVote_Valido_GuardaVoto() throws Exception {
        when(petService.findById("pet-1")).thenReturn(senderPet);
        when(petService.findById("pet-2")).thenReturn(receiverPet);

        assertDoesNotThrow(() -> {
            voteService.vote("user-1", "pet-1", "pet-2");
        });

        verify(voteRepository, times(1)).save(any(Vote.class));
    }

    @Test
    @DisplayName("Unit Test: Votar por la misma mascota lanza ErrorService")
    void testVote_MismaMascota_LanzaErrorService() {
        ErrorService excepcion = assertThrows(ErrorService.class, () -> {
            voteService.vote("user-1", "pet-1", "pet-1");
        });

        assertEquals("No se puede votar por la misma mascota", excepcion.getMessage());
        verifyNoInteractions(voteRepository);
    }

    @Test
    @DisplayName("Unit Test: Intentar votar con mascota de otro usuario lanza ErrorService")
    void testVote_MascotaAjena_LanzaErrorService() throws Exception {
        when(petService.findById("pet-1")).thenReturn(senderPet);

        ErrorService excepcion = assertThrows(ErrorService.class, () -> {
            voteService.vote("usuario-intruso", "pet-1", "pet-2");
        });

        assertEquals("La mascota que vota no pertenece al usuario", excepcion.getMessage());
        verify(voteRepository, never()).save(any(Vote.class));
    }

    @Test
    @DisplayName("Unit Test: Responder voto por el destinatario legítimo actualiza responseDate")
    void testRespond_Exitoso() throws Exception {
        Vote vote = new Vote();
        vote.setId("vote-100");
        vote.setSenderPet(senderPet);
        vote.setReceiverPet(receiverPet);

        when(voteRepository.findById("vote-100")).thenReturn(Optional.of(vote));

        voteService.respond("user-2", "vote-100");

        assertNotNull(vote.getResponseDate(), "La fecha de respuesta debe haber sido asignada");
        verify(voteRepository, times(1)).save(vote);
    }
}

