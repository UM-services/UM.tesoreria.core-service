package um.tesoreria.core.hexagonal.chequera.producto.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.chequera.producto.application.exception.ProductoException;
import um.tesoreria.core.hexagonal.chequera.producto.application.service.ProductoService;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.web.dto.ProductoResponse;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.web.mapper.ProductoDtoMapper;
import um.tesoreria.core.model.view.ProductoTipoChequera;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/producto", "/api/tesoreria/core/producto"})
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final ProductoDtoMapper productoDtoMapper;

    @GetMapping("/")
    public ResponseEntity<List<ProductoResponse>> findAll() {
        List<ProductoResponse> responses = productoService.findAll().stream()
                .map(productoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/bytipochequera/{facultadId}/{lectivoId}/{tipochequeraId}")
    public ResponseEntity<List<ProductoTipoChequera>> findAllByTipoChequera(
            @PathVariable Integer facultadId,
            @PathVariable Integer lectivoId,
            @PathVariable Integer tipochequeraId) {
        return ResponseEntity.ok(
                productoService.findAllByTipoChequera(facultadId, lectivoId, tipochequeraId));
    }

    @GetMapping("/{productoId}")
    public ResponseEntity<ProductoResponse> findByProductoId(@PathVariable Integer productoId) {
        try {
            Producto domain = productoService.findByProductoId(productoId);
            return ResponseEntity.ok(productoDtoMapper.toResponse(domain));
        } catch (ProductoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
