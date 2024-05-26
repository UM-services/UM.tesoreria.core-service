/**
 *
 */
package um.tesoreria.core.controller.facade;

import java.io.FileNotFoundException;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.facade.SheetService;

import static um.tesoreria.core.util.Tool.generateFile;

/**
 * @author daniel
 */
@RestController
@RequestMapping("/sheet")
public class SheetController {

    private final SheetService service;

    @Autowired
    public SheetController(SheetService service) {
        this.service = service;
    }

    @GetMapping("/generateingresos/{anho}/{mes}")
    public ResponseEntity<Resource> generateIngresos(@PathVariable Integer anho, @PathVariable Integer mes)
            throws FileNotFoundException {
        return generateFile(service.generateIngresos(anho, mes), "ingresos.xlsx");
    }

    @PostMapping("/generatedeuda/{facultadId}/{lectivoId}/{soloDeudores}")
    public ResponseEntity<Resource> generateDeuda(@PathVariable Integer facultadId, @PathVariable Integer lectivoId,
                                                  @PathVariable Boolean soloDeudores, @RequestBody List<Integer> tipochequeraIds)
            throws FileNotFoundException {
        return generateFile(service.generateDeuda(facultadId, lectivoId, soloDeudores, tipochequeraIds), "deuda.xlsx");
    }

    @GetMapping("/generateejercicioop/{ejercicioId}")
    public ResponseEntity<Resource> generateEjercicioOp(@PathVariable Integer ejercicioId)
            throws FileNotFoundException {
        return generateFile(service.generateEjercicioOp(ejercicioId), "listaop.xlsx");
    }

    @GetMapping("/generateeficienciapre/{facultadId}/{lectivoId}")
    public ResponseEntity<Resource> generateEficienciaPre(@PathVariable Integer facultadId,
                                                          @PathVariable Integer lectivoId) throws FileNotFoundException {
        return generateFile(service.generateEficienciaPre(facultadId, lectivoId), "eficienciapre.xlsx");
    }

    @GetMapping("/generatecomparativopre/{facultadId}/{lectivoId}")
    public ResponseEntity<Resource> generateComparativoPre(@PathVariable Integer facultadId,
                                                           @PathVariable Integer lectivoId) throws FileNotFoundException {
        return generateFile(service.generateComparativoPre(facultadId, lectivoId), "comparativopre.xlsx");
    }

    @GetMapping("/generatematriculados/{facultadId}/{lectivoId}")
    public ResponseEntity<Resource> generateMatriculados(@PathVariable Integer facultadId,
                                                         @PathVariable Integer lectivoId) throws FileNotFoundException {
        return generateFile(service.generateMatriculados(facultadId, lectivoId), "comparativopre.xlsx");
    }

    @GetMapping("/generateinscriptocurso/{lectivoId}")
    public ResponseEntity<Resource> generateInscriptoCurso(@PathVariable Integer lectivoId)
            throws FileNotFoundException {
        return generateFile(service.generateInscriptoCurso(lectivoId), "inscriptocurso.xlsx");
    }

    @GetMapping("/generateejercicioingreso/{ejercicioId}")
    public ResponseEntity<Resource> generateEjercicioIngreso(@PathVariable Integer ejercicioId)
            throws FileNotFoundException {
        return generateFile(service.generateEjercicioIngreso(ejercicioId), "ingresos.xlsx");
    }

    @GetMapping("/generateSuspendido/{facultadId}/{geograficaId}")
    public ResponseEntity<Resource> generateSuspendido(@PathVariable Integer facultadId,
                                                       @PathVariable Integer geograficaId) throws FileNotFoundException {
        return generateFile(service.generateSuspendido(facultadId, geograficaId), "suspendidos.xlsx");
    }

    @GetMapping("/generateContratados/{anho}/{mes}")
    public ResponseEntity<Resource> generateContratados(@PathVariable Integer anho, @PathVariable Integer mes)
            throws FileNotFoundException {
        return generateFile(service.generateContratados(anho, mes), "contratados.xlsx");
    }

    @GetMapping("/generateLibroIva/{fechaDesde}/{fechaHasta}")
    public ResponseEntity<Resource> generateLibroIva(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaDesde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaHasta) throws FileNotFoundException {
        return generateFile(service.generateLibroIva(fechaDesde, fechaHasta), "libro.xlsx");
    }

    @GetMapping("/generateProveedores")
    public ResponseEntity<Resource> generateProveedores() throws FileNotFoundException {
        return generateFile(service.generateProveedores(), "proveedores.xlsx");
    }

    @GetMapping("/generateFacturasPendientes/{fechaDesde}/{fechaHasta}")
    public ResponseEntity<Resource> generateFacturasPendientes(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaDesde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaHasta) throws FileNotFoundException {
        return generateFile(service.generateFacturasPendientes(fechaDesde, fechaHasta), "pendientes.xlsx");
    }

}
