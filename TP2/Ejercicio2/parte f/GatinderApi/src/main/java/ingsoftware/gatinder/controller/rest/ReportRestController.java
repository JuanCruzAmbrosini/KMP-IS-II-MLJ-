package ingsoftware.gatinder.controller.rest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ingsoftware.gatinder.dto.VoteReportDto;
import ingsoftware.gatinder.service.ErrorService;
import ingsoftware.gatinder.service.VoteService;

@RestController
@RequestMapping("/api/reports")
public class ReportRestController {

    @Autowired
    private VoteService voteService;

    @GetMapping("/votes")
    public ResponseEntity<List<VoteReportDto>> getVotesReportJson() throws ErrorService {
        return ResponseEntity.ok(voteService.buildVoteReport());
    }

    @GetMapping("/votes/download")
    public ResponseEntity<byte[]> downloadVotesReportFile() throws ErrorService {
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
