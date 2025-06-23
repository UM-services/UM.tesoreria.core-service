/**
 *
 */
package um.tesoreria.core.controller.facade;

import um.tesoreria.core.model.dto.AsignacionCostoDto;
import um.tesoreria.core.service.facade.CostoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author daniel
 */
@RestController
@RequestMapping("/costo")
@Slf4j
public class CostoController {

    @Autowired
    private CostoService service;

    @PostMapping("/addAsignacion")
    public ResponseEntity<Boolean> addAsignacion(@RequestBody AsignacionCostoDto asignacionCostoDto) throws JsonProcessingException {
        log.debug("AsignacionCosto - {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(asignacionCostoDto));
        return new ResponseEntity<>(service.addAsignacion(asignacionCostoDto), HttpStatus.OK);
    }

    @GetMapping("/deleteDesignacion/{entregaId}")
    public ResponseEntity<Boolean> deleteAsignacion(@PathVariable Long entregaId) {
        log.debug("DeleteDesignacion - {}", entregaId);
        return new ResponseEntity<>(service.deleteDesignacion(entregaId), HttpStatus.OK);
    }

    @GetMapping("/depurarGastosProveedor/{proveedorId}/{geograficaId}")
    public ResponseEntity<Boolean> depurarGastosProveedor(@PathVariable Integer proveedorId, @PathVariable Integer geograficaId) {
        log.debug("DepurarGastosProveedor - {}/{}", proveedorId, geograficaId);
        return new ResponseEntity<>(service.depurarGastosProveedor(proveedorId, geograficaId), HttpStatus.OK);
    }

    @GetMapping("/recalcularAsignado/{proveedorArticuloId}")
    public ResponseEntity<Void> recalcularAsignado(@PathVariable Long proveedorArticuloId) {
        log.debug("Recalcular Asignado {}", proveedorArticuloId);
        service.recalcularAsignado(proveedorArticuloId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
