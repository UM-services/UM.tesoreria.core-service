package um.tesoreria.core.controller.facade;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.service.facade.ContabilidadService;

@RestController
@RequestMapping("/contabilidad")
public class ContabilidadController {

    private final ContabilidadService service;

    public ContabilidadController(ContabilidadService service) {
        this.service = service;
    }

    @GetMapping("/ajusteAsientoCosto/{proveedorMovimientoIdFactura}")
    public ResponseEntity<Boolean> ajusteAsientoCosto(@PathVariable Long proveedorMovimientoIdFactura) {
        return new ResponseEntity<>(service.ajusteAsientoCosto(proveedorMovimientoIdFactura), HttpStatus.OK);
    }
}
