package ingsoftware.gatinder.service;

import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.time.Instant;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.entity.Vote;
import ingsoftware.gatinder.repository.VoteRepository;


@Service
public class VoteService {
    @Autowired private PetService petService;
    @Autowired private VoteRepository voteRepository;

    @Transactional public void vote(String userId, String senderPetId, String receiverPetId) throws ErrorService {
        try {
            Vote vote = new Vote();
            vote.setId(UUID.randomUUID().toString());
            vote.setDate(Instant.now());
            if (senderPetId.equals(receiverPetId)) {
                throw new ErrorService("No se puede votar por la misma mascota");
            }
            Pet senderPet = petService.findById(senderPetId);
            if (!senderPet.getUser().getId().equals(userId)) {
                throw new ErrorService("La mascota que vota no pertenece al usuario");
            }
            Pet receiverPet = petService.findById(receiverPetId);
            vote.setSenderPet(senderPet);
            vote.setReceiverPet(receiverPet);
            voteRepository.save(vote);
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al registrar el voto");
        }
    }

    @Transactional public void respond(String userId, String voteId) throws ErrorService {
        try {
            Optional<Vote> response = voteRepository.findById(voteId);
            if (response.isPresent()) {
                Vote vote = response.get();
                Pet receiverPet = vote.getReceiverPet();
                if (!receiverPet.getUser().getId().equals(userId)) {
                    throw new ErrorService("El usuario no tiene permiso para responder este voto");
                }
                vote.setResponseDate(Instant.now());
                voteRepository.save(vote);
            } else {
                throw new ErrorService("No se encontró el voto con el ID proporcionado");
            }
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al responder el voto");
        }
    }

    public List<Vote> findAll() throws ErrorService {
        try {
            return voteRepository.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al listar los votos");
        }
    }

    public Vote findById(String voteId) throws ErrorService {
        try {
            Optional<Vote> response = voteRepository.findById(voteId);
            if (response.isPresent()) {
                return response.get();
            } else {
                throw new ErrorService("No se encontró el voto con el ID proporcionado");
            }
        } catch (ErrorService e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al obtener el voto");
        }
    }

}
