/**
 *
 */
package um.tesoreria.core.controller.facade;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.exception.AsientoConflictoException;
import um.tesoreria.core.model.dto.AsignacionCostoDto;
import um.tesoreria.core.service.facade.CostoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author daniel
 */
@RestController
@RequestMapping({"/costo", "/api/tesoreria/core/costo"})
@Slf4j
@RequiredArgsConstructor
public class CostoController {

    private final CostoService service;

    @ExceptionHandler(AsientoConflictoException.class)
    public ResponseEntity<Map<String, Object>> asientoConflicto(AsientoConflictoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "status", 409,
                "error", "Conflict",
                "message", ex.getMessage()));
    }

    @PostMapping("/addAsignacion")
    public ResponseEntity<Boolean> addAsignacion(@RequestBody AsignacionCostoDto asignacionCostoDto) {
        return ResponseEntity.ok(service.addAsignacion(asignacionCostoDto));
    }

    @GetMapping("/deleteDesignacion/{entregaId}")
    public ResponseEntity<Boolean> deleteAsignacion(@PathVariable Long entregaId) {
        log.debug("DeleteDesignacion - {}", entregaId);
        return ResponseEntity.ok(service.deleteDesignacion(entregaId));
    }

    @GetMapping("/depurarGastosProveedor/{proveedorId}/{geograficaId}")
    public ResponseEntity<Boolean> depurarGastosProveedor(@PathVariable Integer proveedorId, @PathVariable Integer geograficaId) {
        log.debug("DepurarGastosProveedor - {}/{}", proveedorId, geograficaId);
        return ResponseEntity.ok(service.depurarGastosProveedor(proveedorId, geograficaId));
    }

    @GetMapping("/recalcularAsignado/{proveedorArticuloId}")
    public ResponseEntity<Void> recalcularAsignado(@PathVariable Long proveedorArticuloId) {
        log.debug("Recalcular Asignado {}", proveedorArticuloId);
        service.recalcularAsignado(proveedorArticuloId);
        return ResponseEntity.ok().build();
    }

}
