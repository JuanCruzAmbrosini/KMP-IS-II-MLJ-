package ingsoftware.gatinder.service;

import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.Instant;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ingsoftware.gatinder.entity.Pet;
import ingsoftware.gatinder.entity.Vote;
import ingsoftware.gatinder.repository.VoteRepository;
import ingsoftware.gatinder.dto.VoteReportDto;


@Service
public class VoteService {
    @Autowired private PetService petService;
    @Autowired private VoteRepository voteRepository;
    @Autowired(required = false) private ingsoftware.gatinder.mapper.VoteMapper voteMapper;

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

    public List<VoteReportDto> buildVoteReport() throws ErrorService {
        try {
            Map<String, VoteReportDto> report = new LinkedHashMap<>();
            for (Vote vote : voteRepository.findAll()) {
                Pet pet = vote.getReceiverPet();
                String petId = pet.getId();
                VoteReportDto current = report.get(petId);
                long count = current == null ? 1 : current.getVoteCount() + 1;
                report.put(petId, new VoteReportDto(pet.getUser().getFirstName(),
                        pet.getUser().getLastName(), pet.getName(), count));
            }
            return new ArrayList<>(report.values());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorService("Error al generar el reporte de votos");
        }
    }

    public ingsoftware.gatinder.dto.VoteDto toDto(Vote vote) {
        if (vote == null) return null;
        if (voteMapper != null) return voteMapper.toDto(vote);
        String senderId = vote.getSenderPet() != null ? vote.getSenderPet().getId() : null;
        String senderName = vote.getSenderPet() != null ? vote.getSenderPet().getName() : null;
        String receiverId = vote.getReceiverPet() != null ? vote.getReceiverPet().getId() : null;
        String receiverName = vote.getReceiverPet() != null ? vote.getReceiverPet().getName() : null;
        return new ingsoftware.gatinder.dto.VoteDto(vote.getId(), vote.getDate(), vote.getResponseDate(), senderId, senderName, receiverId, receiverName);
    }

    public List<ingsoftware.gatinder.dto.VoteDto> findAllDtos() throws ErrorService {
        List<Vote> votes = findAll();
        if (voteMapper != null) return voteMapper.toDtoList(votes);
        List<ingsoftware.gatinder.dto.VoteDto> dtos = new java.util.ArrayList<>();
        for (Vote v : votes) dtos.add(toDto(v));
        return dtos;
    }

    public ingsoftware.gatinder.dto.VoteDto findDtoById(String voteId) throws ErrorService {
        return toDto(findById(voteId));
    }

    @Transactional public ingsoftware.gatinder.dto.VoteDto voteAndGetDto(String userId, String senderPetId, String receiverPetId) throws ErrorService {
        vote(userId, senderPetId, receiverPetId);
        List<Vote> votes = voteRepository.findAll();
        Vote created = votes.isEmpty() ? null : votes.get(votes.size() - 1);
        return toDto(created);
    }
}
