package um.tesoreria.core.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.kotlin.model.view.OrdenPagoDetalle;
import um.tesoreria.core.service.view.OrdenPagoDetalleService;

import java.util.List;

@RestController
@RequestMapping("/ordenPagoDetalle")
@RequiredArgsConstructor
public class OrdenPagoDetalleController {

    private final OrdenPagoDetalleService service;

    @PostMapping("/search")
    public ResponseEntity<List<OrdenPagoDetalle>> findByStrings(@RequestBody List<String> conditions) {
        return ResponseEntity.ok(service.findAllByStrings(conditions));
    }

}
