package um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.hexagonal.umhub.campanha.application.service.CampanhaService;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.web.dto.CampanhaRequest;
import um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.web.dto.CampanhaResponse;
import um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.web.mapper.CampanhaDtoMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/umhub/campanha")
@RequiredArgsConstructor
public class CampanhaController {

    private final CampanhaService campanhaService;
    private final CampanhaDtoMapper campanhaDtoMapper;

    @GetMapping("/{campanhaId}")
    public ResponseEntity<CampanhaResponse> findByCampanhaId(@PathVariable UUID campanhaId) {
        return campanhaService.getCampanhaById(campanhaId)
                .map(domain -> new ResponseEntity<>(campanhaDtoMapper.toResponse(domain), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/")
    public ResponseEntity<CampanhaResponse> add(@RequestBody CampanhaRequest request) {
        Campanha domain = campanhaDtoMapper.toDomain(request);
        Campanha created = campanhaService.createCampanha(domain);
        return new ResponseEntity<>(campanhaDtoMapper.toResponse(created), HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<List<CampanhaResponse>> getAll() {
        List<CampanhaResponse> responses = campanhaService.getAllCampanhas().stream()
                .map(campanhaDtoMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PutMapping("/{campanhaId}")
    public ResponseEntity<CampanhaResponse> update(@PathVariable UUID campanhaId, @RequestBody CampanhaRequest request) {
        Campanha domain = campanhaDtoMapper.toDomain(request);
        return campanhaService.updateCampanha(campanhaId, domain)
                .map(updated -> new ResponseEntity<>(campanhaDtoMapper.toResponse(updated), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{campanhaId}")
    public ResponseEntity<Void> delete(@PathVariable UUID campanhaId) {
        boolean deleted = campanhaService.deleteCampanha(campanhaId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
