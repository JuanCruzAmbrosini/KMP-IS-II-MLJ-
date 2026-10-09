package ingsoftware.gatinder.controller.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ingsoftware.gatinder.dto.VoteDto;
import ingsoftware.gatinder.dto.VoteReportDto;
import ingsoftware.gatinder.dto.VoteRequestDto;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.VoteService;

@RestController
@RequestMapping("/api/votes")
public class VoteRestController {

    @Autowired
    private VoteService voteService;

    @GetMapping
    public ResponseEntity<List<VoteDto>> getAllVotes() throws ErrorService {
        return ResponseEntity.ok(voteService.findAllDtos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VoteDto> getVoteById(@PathVariable String id) throws ErrorService {
        return ResponseEntity.ok(voteService.findDtoById(id));
    }

    @GetMapping("/report")
    public ResponseEntity<List<VoteReportDto>> getVoteReport() throws ErrorService {
        return ResponseEntity.ok(voteService.buildVoteReport());
    }

    @PostMapping
    public ResponseEntity<VoteDto> emitVote(
            @RequestParam("userId") String userId,
            @RequestBody VoteRequestDto request) throws ErrorService {
        VoteDto created = voteService.voteAndGetDto(userId, request.getSenderPetId(), request.getReceiverPetId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/respond")
    public ResponseEntity<VoteDto> respondVote(
            @PathVariable String id,
            @RequestParam("userId") String userId) throws ErrorService {
        voteService.respond(userId, id);
        return ResponseEntity.ok(voteService.findDtoById(id));
    }
}
