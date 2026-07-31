package um.tesoreria.core.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.kotlin.model.view.TipoChequeraFacultadLectivoGeograficaCantidad;
import um.tesoreria.core.service.view.TipoChequeraFacultadLectivoGeograficaCantidadService;

import java.util.List;

@RestController
@RequestMapping("/tipoChequeraFacultadLectivoGeografica")
@RequiredArgsConstructor
public class TipoChequeraFacultadLectivoGeograficaCantidadController {

    private final TipoChequeraFacultadLectivoGeograficaCantidadService service;

    @GetMapping("/sede/{facultadId}/{lectivoId}/{geograficaId}")
    public ResponseEntity<List<TipoChequeraFacultadLectivoGeograficaCantidad>> findAllBySede(@PathVariable Integer facultadId, @PathVariable Integer lectivoId, @PathVariable Integer geograficaId) {
        return ResponseEntity.ok(service.findAllBySede(facultadId, lectivoId, geograficaId));
    }

}
