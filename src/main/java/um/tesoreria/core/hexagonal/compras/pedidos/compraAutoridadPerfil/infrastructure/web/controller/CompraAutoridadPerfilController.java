package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.exception.CompraAutoridadPerfilException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.service.CompraAutoridadPerfilService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.dto.CompraAutoridadPerfilRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.dto.CompraAutoridadPerfilResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.mapper.CompraAutoridadPerfilDtoMapper;

import java.util.List;

/**
 * Perfiles de autoridad por monto (múltiplo de la referencia del ejercicio; nulo = ilimitado).
 */
@RestController
@RequestMapping("/api/tesoreria/core/compraAutoridadPerfil")
@RequiredArgsConstructor
public class CompraAutoridadPerfilController {

    private final CompraAutoridadPerfilService compraAutoridadPerfilService;
    private final CompraAutoridadPerfilDtoMapper compraAutoridadPerfilDtoMapper;

    @GetMapping
    public ResponseEntity<List<CompraAutoridadPerfilResponse>> findAll() {
        List<CompraAutoridadPerfilResponse> responses = compraAutoridadPerfilService.findAll().stream()
                .map(compraAutoridadPerfilDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{autoridadPerfilId}")
    public ResponseEntity<CompraAutoridadPerfilResponse> findById(@PathVariable Long autoridadPerfilId) {
        return compraAutoridadPerfilService.findById(autoridadPerfilId)
                .map(compraAutoridadPerfilDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Perfil de autoridad no encontrado: " + autoridadPerfilId));
    }

    @PostMapping
    public ResponseEntity<CompraAutoridadPerfilResponse> add(@Valid @RequestBody CompraAutoridadPerfilRequest request) {
        try {
            CompraAutoridadPerfil created = compraAutoridadPerfilService.add(compraAutoridadPerfilDtoMapper.toDomain(request));
            return ResponseEntity.ok(compraAutoridadPerfilDtoMapper.toResponse(created));
        } catch (CompraAutoridadPerfilException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{autoridadPerfilId}")
    public ResponseEntity<CompraAutoridadPerfilResponse> update(@Valid @RequestBody CompraAutoridadPerfilRequest request,
                                                                @PathVariable Long autoridadPerfilId) {
        try {
            CompraAutoridadPerfil updated = compraAutoridadPerfilService.update(
                    compraAutoridadPerfilDtoMapper.toDomain(request), autoridadPerfilId);
            return ResponseEntity.ok(compraAutoridadPerfilDtoMapper.toResponse(updated));
        } catch (CompraAutoridadPerfilException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{autoridadPerfilId}")
    public ResponseEntity<Void> delete(@PathVariable Long autoridadPerfilId) {
        try {
            compraAutoridadPerfilService.delete(autoridadPerfilId);
            return ResponseEntity.noContent().build();
        } catch (CompraAutoridadPerfilException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

}
