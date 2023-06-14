package um.tesoreria.rest.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.rest.kotlin.model.view.TipoChequeraFacultadLectivoGeograficaCantidad;
import um.tesoreria.rest.service.view.TipoChequeraFacultadLectivoGeograficaCantidadService;

import java.util.List;

@RestController
@RequestMapping("/tipoChequeraFacultadLectivoGeografica")
public class TipoChequeraFacultadLectivoGeograficaCantidadController {

    @Autowired
    private TipoChequeraFacultadLectivoGeograficaCantidadService service;

    @GetMapping("/sede/{facultadId}/{lectivoId}/{geograficaId}")
    public ResponseEntity<List<TipoChequeraFacultadLectivoGeograficaCantidad>> findAllBySede(@PathVariable Integer facultadId, @PathVariable Integer lectivoId, @PathVariable Integer geograficaId) {
        return new ResponseEntity<>(service.findAllBySede(facultadId, lectivoId, geograficaId), HttpStatus.OK);
    }

}
