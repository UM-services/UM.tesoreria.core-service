package um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.hexagonal.compras.proveedor.application.exception.ProveedorConflictoException;
import um.tesoreria.core.hexagonal.compras.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.dto.ProveedorErrorResponse;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.dto.ProveedorRequest;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.dto.ProveedorResponse;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.dto.ProveedorSearchResponse;



import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.mapper.ProveedorDtoMapper;

import java.util.List;
import java.util.stream.Collectors;
import um.tesoreria.core.model.PageRequest;
import um.tesoreria.core.model.PaginatedResponse;

@RestController
@RequestMapping({"/proveedor", "/api/tesoreria/core/proveedor"})
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final ProveedorDtoMapper proveedorDtoMapper;



    @PostMapping("/page")
    public ResponseEntity<PaginatedResponse<ProveedorResponse>> findPaginated(
            @RequestBody(required = false) PageRequest pageRequest) {
        int page = pageRequest == null || pageRequest.page() == null ? 0 : pageRequest.page();
        int size = pageRequest == null || pageRequest.size() == null ? 50 : pageRequest.size();
        PaginatedResponse<Proveedor> result = proveedorService.getPaginated(page, size);
        List<ProveedorResponse> responses = result.getData().stream()
                .map(proveedorDtoMapper::toResponse)
                .collect(Collectors.toList());
        PaginatedResponse<ProveedorResponse> paginatedResponse = new PaginatedResponse<>(
                responses, result.getTotalElements(), result.getTotalPages(), result.getCurrentPage(), result.getPageSize());
        return ResponseEntity.ok(paginatedResponse);
    }

    @GetMapping("/")
    public ResponseEntity<List<ProveedorResponse>> findAll() {
        List<ProveedorResponse> responses = proveedorService.getAll().stream()
                .map(proveedorDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/search")
    public ResponseEntity<List<ProveedorSearchResponse>> findAllByStrings(@RequestBody List<String> conditions) {
        List<ProveedorSearchResponse> responses = proveedorService.search(conditions).stream()
                .map(proveedorDtoMapper::toSearchResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{proveedorId}")
    public ResponseEntity<ProveedorResponse> findByProveedorId(@PathVariable Integer proveedorId) {
        return proveedorService.getByProveedorId(proveedorId)
                .map(proveedor -> ResponseEntity.ok(proveedorDtoMapper.toResponse(proveedor)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
    }

    @GetMapping("/cuit/{cuit}")
    public ResponseEntity<ProveedorResponse> findByCuit(@PathVariable String cuit) {
        return proveedorService.getByCuit(cuit)
                .map(proveedor -> ResponseEntity.ok(proveedorDtoMapper.toResponse(proveedor)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado con CUIT " + cuit));
    }

    @GetMapping("/last")
    public ResponseEntity<ProveedorResponse> findLast() {
        return proveedorService.getLast()
                .map(proveedor -> ResponseEntity.ok(proveedorDtoMapper.toResponse(proveedor)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay proveedores"));
    }

    @PostMapping("/")
    public ResponseEntity<?> add(@RequestBody ProveedorRequest proveedorRequest) {
        try {
            Proveedor proveedor = proveedorDtoMapper.toDomain(proveedorRequest);
            Proveedor createdProveedor = proveedorService.create(proveedor);
            return ResponseEntity.ok(proveedorDtoMapper.toResponse(createdProveedor));
        } catch (ProveedorConflictoException e) {
            return error(HttpStatus.CONFLICT, e);
        }
    }

    @PutMapping("/{proveedorId}")
    public ResponseEntity<?> update(@RequestBody ProveedorRequest proveedorRequest, @PathVariable Integer proveedorId) {
        try {
            Proveedor proveedor = proveedorDtoMapper.toDomain(proveedorRequest);
            return proveedorService.update(proveedorId, proveedor)
                    .<ResponseEntity<?>>map(updated -> ResponseEntity.ok(proveedorDtoMapper.toResponse(updated)))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se pudo actualizar"));
        } catch (ProveedorConflictoException e) {
            return error(HttpStatus.CONFLICT, e);
        }
    }

    /**
     * Baja física. Libre: 204 sin cuerpo. Inexistente: 404. Con registros asociados: 409 con cuerpo JSON
     * (VB6 toma como éxito cualquier respuesta de cuerpo vacío, por eso el conflicto nunca va vacío).
     */
    @DeleteMapping("/{proveedorId}")
    public ResponseEntity<?> delete(@PathVariable Integer proveedorId) {
        try {
            if (proveedorService.delete(proveedorId)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ProveedorConflictoException e) {
            return error(HttpStatus.CONFLICT, e);
        }
    }

    private static ResponseEntity<ProveedorErrorResponse> error(HttpStatus status, RuntimeException e) {
        return ResponseEntity.status(status)
                .body(new ProveedorErrorResponse(status.value(), status.getReasonPhrase(), e.getMessage()));
    }
}