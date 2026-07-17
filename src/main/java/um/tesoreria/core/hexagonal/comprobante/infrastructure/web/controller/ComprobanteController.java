package um.tesoreria.core.hexagonal.comprobante.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.comprobante.application.exception.ComprobanteException;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.web.dto.ComprobanteResponse;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.web.mapper.ComprobanteDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/comprobante", "/api/tesoreria/core/comprobante"})
@RequiredArgsConstructor
public class ComprobanteController {

    private final ComprobanteService comprobanteService;
    private final ComprobanteDtoMapper comprobanteDtoMapper;

    @GetMapping("/")
    public ResponseEntity<List<ComprobanteResponse>> findAll() {
        List<ComprobanteResponse> responses = comprobanteService.findAll().stream()
                .map(comprobanteDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{comprobanteId}")
    public ResponseEntity<ComprobanteResponse> findByComprobanteId(@PathVariable Integer comprobanteId) {
        try {
            var domain = comprobanteService.findByComprobanteId(comprobanteId);
            return ResponseEntity.ok(comprobanteDtoMapper.toResponse(domain));
        } catch (ComprobanteException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
