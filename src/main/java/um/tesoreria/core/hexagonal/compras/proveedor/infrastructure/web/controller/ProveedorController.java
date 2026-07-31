package um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.hexagonal.compras.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.dto.ProveedorRequest;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.dto.ProveedorResponse;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.dto.ProveedorSearchResponse;



import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.mapper.ProveedorDtoMapper;

import java.util.List;
import java.util.stream.Collectors;
import um.tesoreria.core.model.PaginatedResponse;

@RestController
@RequestMapping({"/proveedor", "/api/tesoreria/core/proveedor"})
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final ProveedorDtoMapper proveedorDtoMapper;
    
    

    @GetMapping("/page")
    public ResponseEntity<PaginatedResponse<ProveedorResponse>> findPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
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
    public ResponseEntity<ProveedorResponse> add(@RequestBody ProveedorRequest proveedorRequest) {
        Proveedor proveedor = proveedorDtoMapper.toDomain(proveedorRequest);
        Proveedor createdProveedor = proveedorService.create(proveedor);
        return ResponseEntity.ok(proveedorDtoMapper.toResponse(createdProveedor));
    }

    @PutMapping("/{proveedorId}")
    public ResponseEntity<ProveedorResponse> update(@RequestBody ProveedorRequest proveedorRequest, @PathVariable Integer proveedorId) {
        Proveedor proveedor = proveedorDtoMapper.toDomain(proveedorRequest);
        return proveedorService.update(proveedorId, proveedor)
                .map(updated -> ResponseEntity.ok(proveedorDtoMapper.toResponse(updated)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No se pudo actualizar"));
    }

    @DeleteMapping("/{proveedorId}")
    public ResponseEntity<Void> delete(@PathVariable Integer proveedorId) {
        if (proveedorService.delete(proveedorId)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
