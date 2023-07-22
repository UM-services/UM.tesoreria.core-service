package um.tesoreria.rest.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.rest.kotlin.model.view.OrdenPagoDetalle;
import um.tesoreria.rest.service.view.OrdenPagoDetalleService;

import java.util.List;

@RestController
@RequestMapping("/ordenPagoDetalle")
public class OrdenPagoDetalleController {

    @Autowired
    private OrdenPagoDetalleService service;

    @PostMapping("/search")
    public ResponseEntity<List<OrdenPagoDetalle>> findByStrings(@RequestBody List<String> conditions) {
        return new ResponseEntity<>(service.findAllByStrings(conditions), HttpStatus.OK);
    }

}
