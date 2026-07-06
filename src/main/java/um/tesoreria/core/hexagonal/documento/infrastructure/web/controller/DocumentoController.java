package um.tesoreria.core.hexagonal.documento.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.documento.application.exception.DocumentoException;
import um.tesoreria.core.hexagonal.documento.application.service.DocumentoService;
import um.tesoreria.core.hexagonal.documento.infrastructure.web.dto.DocumentoResponse;
import um.tesoreria.core.hexagonal.documento.infrastructure.web.mapper.DocumentoDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/documento")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;
    private final DocumentoDtoMapper documentoDtoMapper;

    @GetMapping("/")
    public ResponseEntity<List<DocumentoResponse>> findAll() {
        List<DocumentoResponse> responses = documentoService.findAll().stream()
                .map(documentoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{documentoId}")
    public ResponseEntity<DocumentoResponse> findByDocumentoId(@PathVariable Integer documentoId) {
        try {
            var domain = documentoService.findByDocumentoId(documentoId);
            return ResponseEntity.ok(documentoDtoMapper.toResponse(domain));
        } catch (DocumentoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
