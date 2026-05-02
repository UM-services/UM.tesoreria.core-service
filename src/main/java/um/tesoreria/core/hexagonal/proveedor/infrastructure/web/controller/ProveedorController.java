package um.tesoreria.core.hexagonal.proveedor.infrastructure.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.hexagonal.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.web.dto.ProveedorRequest;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.web.dto.ProveedorResponse;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.web.dto.ProveedorSearchResponse;
import um.tesoreria.core.hexagonal.proveedor.infrastructure.web.mapper.ProveedorDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/proveedor", "/api/tesoreria/core/proveedor"})
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final ProveedorDtoMapper proveedorDtoMapper;

    public ProveedorController(ProveedorService proveedorService, ProveedorDtoMapper proveedorDtoMapper) {
        this.proveedorService = proveedorService;
        this.proveedorDtoMapper = proveedorDtoMapper;
    }

    @GetMapping("/")
    public ResponseEntity<List<ProveedorResponse>> findAll() {
        List<ProveedorResponse> responses = proveedorService.getAll().stream()
                .map(proveedorDtoMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<ProveedorSearchResponse>> findAllByStrings(@RequestBody List<String> conditions) {
        List<ProveedorSearchResponse> responses = proveedorService.search(conditions).stream()
                .map(proveedorDtoMapper::toSearchResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{proveedorId}")
    public ResponseEntity<ProveedorResponse> findByProveedorId(@PathVariable Integer proveedorId) {
        return proveedorService.getByProveedorId(proveedorId)
                .map(proveedor -> new ResponseEntity<>(proveedorDtoMapper.toResponse(proveedor), HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado"));
    }

    @GetMapping("/cuit/{cuit}")
    public ResponseEntity<ProveedorResponse> findByCuit(@PathVariable String cuit) {
        return proveedorService.getByCuit(cuit)
                .map(proveedor -> new ResponseEntity<>(proveedorDtoMapper.toResponse(proveedor), HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado con CUIT " + cuit));
    }

    @GetMapping("/last")
    public ResponseEntity<ProveedorResponse> findLast() {
        return proveedorService.getLast()
                .map(proveedor -> new ResponseEntity<>(proveedorDtoMapper.toResponse(proveedor), HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay proveedores"));
    }

    @PostMapping("/")
    public ResponseEntity<ProveedorResponse> add(@RequestBody ProveedorRequest proveedorRequest) {
        Proveedor proveedor = proveedorDtoMapper.toDomain(proveedorRequest);
        Proveedor createdProveedor = proveedorService.create(proveedor);
        return new ResponseEntity<>(proveedorDtoMapper.toResponse(createdProveedor), HttpStatus.OK);
    }

    @PutMapping("/{proveedorId}")
    public ResponseEntity<ProveedorResponse> update(@RequestBody ProveedorRequest proveedorRequest, @PathVariable Integer proveedorId) {
        Proveedor proveedor = proveedorDtoMapper.toDomain(proveedorRequest);
        return proveedorService.update(proveedorId, proveedor)
                .map(updated -> new ResponseEntity<>(proveedorDtoMapper.toResponse(updated), HttpStatus.OK))
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
