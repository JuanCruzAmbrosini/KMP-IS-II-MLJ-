package ingsoftware.gatinder.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ingsoftware.gatinder.dto.UserDto;
import ingsoftware.gatinder.dto.VoteReportDto;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.VoteService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/reports")
public class ReportController {
    @Autowired private VoteService voteService;

    @GetMapping(value = "/votes", produces = "text/plain")
    public ResponseEntity<byte[]> downloadVotes(HttpSession session) throws ErrorService {
        UserDto loggedUser = (UserDto) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, "/login").build();
        }

        List<VoteReportDto> report = voteService.buildVoteReport();
        StringBuilder content = new StringBuilder("Nombre\tApellido\tMascota\tCantidad de votos\n");
        for (VoteReportDto row : report) {
            content.append(row.getFirstName()).append('\t')
                    .append(row.getLastName()).append('\t')
                    .append(row.getPetName()).append('\t')
                    .append(row.getVoteCount()).append('\n');
        }
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=gatinder-votos.txt")
                .body(content.toString().getBytes(StandardCharsets.UTF_8));
    }
}