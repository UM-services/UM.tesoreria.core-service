/**
 *
 */
package um.tesoreria.core.controller.facade;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.model.dto.AsignacionCostoDto;
import um.tesoreria.core.service.facade.CostoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author daniel
 */
@RestController
@RequestMapping("/costo")
@Slf4j
@RequiredArgsConstructor
public class CostoController {

    private final CostoService service;

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
