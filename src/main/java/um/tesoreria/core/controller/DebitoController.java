/**
 * 
 */
package um.tesoreria.core.controller;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.exception.DebitoException;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.service.DebitoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/debito")
@RequiredArgsConstructor
public class DebitoController {

	private final DebitoService service;

	@GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{debitoTipoId}")
	public ResponseEntity<List<Debito>> findAllByChequera(@PathVariable Integer facultadId,
                                                          @PathVariable Integer tipoChequeraId,
                                                          @PathVariable Long chequeraSerieId,
                                                          @PathVariable Integer debitoTipoId) {
        return ResponseEntity.ok(service.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId, debitoTipoId));
	}

	@GetMapping("/alternativa/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}/{debitoTipoId}")
	public ResponseEntity<List<Debito>> findAllByEnviados(@PathVariable Integer facultadId,
                                                          @PathVariable Integer tipoChequeraId,
                                                          @PathVariable Long chequeraSerieId,
                                                          @PathVariable Integer alternativaId,
                                                          @PathVariable Integer debitoTipoId) {
        return ResponseEntity.ok(service.findAllByAlternativa(facultadId, tipoChequeraId, chequeraSerieId, alternativaId, debitoTipoId));
	}

	@GetMapping("/envio/{fechaEnvio}/{soloSantander}/{soloOtrosBancos}/{debitoTipoId}")
	public ResponseEntity<List<Debito>> findAllByFechaEnvio(@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime fechaEnvio,
                                                            @PathVariable Boolean soloSantander,
                                                            @PathVariable Boolean soloOtrosBancos,
                                                            @PathVariable Integer debitoTipoId) {
        return ResponseEntity.ok(service.findAllByFechaEnvio(fechaEnvio, soloSantander, soloOtrosBancos, debitoTipoId));
	}

	@GetMapping("/noincluidos/{fecha}/{debitoTipoId}")
	public ResponseEntity<List<Debito>> findAllNoIncluidos(@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime fecha,
                                                           @PathVariable Integer debitoTipoId) {
        return ResponseEntity.ok(service.findAllNoIncluidos(fecha, debitoTipoId));
	}

	@GetMapping("/noenviadosaltas/{desde}/{hasta}/{debitoTipoId}")
	public ResponseEntity<List<Debito>> findAllNoEnviadosAltas(@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime desde,
                                                               @PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime hasta,
                                                               @PathVariable Integer debitoTipoId) {
        return ResponseEntity.ok(service.findAllNoEnviadosAltas(desde, hasta, debitoTipoId));
	}

	@GetMapping("/enviados/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}/{debitoTipoId}")
	public ResponseEntity<List<Debito>> findAllByEnviados(@PathVariable Integer facultadId,
                                                          @PathVariable Integer tipoChequeraId,
                                                          @PathVariable Long chequeraSerieId,
                                                          @PathVariable Integer productoId,
                                                          @PathVariable Integer alternativaId,
                                                          @PathVariable Integer debitoTipoId) {
        return ResponseEntity.ok(service.findAllByEnviados(facultadId, tipoChequeraId, chequeraSerieId,
                productoId, alternativaId, debitoTipoId));
	}

	@GetMapping("/pendienteschequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{fechaVencimiento}")
	public ResponseEntity<List<Debito>> findAllPendientesChequera(@PathVariable Integer facultadId,
                                                                  @PathVariable Integer tipoChequeraId,
                                                                  @PathVariable Long chequeraSerieId,
                                                                  @PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime fechaVencimiento) {
        return ResponseEntity.ok(service.findAllPendientesChequera(facultadId, tipoChequeraId, chequeraSerieId, fechaVencimiento));
	}

	@GetMapping("/pendientes/{desde}/{hasta}/{soloSantander}/{soloOtrosBancos}/{debitoTipoId}")
	public ResponseEntity<List<Debito>> findAllPendientes(@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime desde,
                                                          @PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime hasta,
                                                          @PathVariable Boolean soloSantander, @PathVariable Boolean soloOtrosBancos,
                                                          @PathVariable Integer debitoTipoId) {
        return ResponseEntity.ok(service.findAllPendientes(desde, hasta, soloSantander, soloOtrosBancos, debitoTipoId));
	}

	@GetMapping("/asociados/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}/{cuotaId}")
	public ResponseEntity<List<Debito>> findAsociados(@PathVariable Integer facultadId,
                                                      @PathVariable Integer tipoChequeraId,
                                                      @PathVariable Long chequeraSerieId,
                                                      @PathVariable Integer productoId,
                                                      @PathVariable Integer alternativaId,
                                                      @PathVariable Integer cuotaId) {
        return ResponseEntity.ok(service.findAllAsociados(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId));
	}

	@GetMapping("/cuota/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}/{cuotaId}/{debitoTipoId}")
	public ResponseEntity<Debito> findByCuota(@PathVariable Integer facultadId,
                                              @PathVariable Integer tipoChequeraId,
                                              @PathVariable Long chequeraSerieId,
                                              @PathVariable Integer productoId,
                                              @PathVariable Integer alternativaId,
                                              @PathVariable Integer cuotaId,
                                              @PathVariable Integer debitoTipoId) {
		try {
            return ResponseEntity.ok(service.findByCuota(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId, debitoTipoId));
		} catch (DebitoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/last/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
	public ResponseEntity<Debito> findLastByChequera(@PathVariable Integer facultadId,
                                                     @PathVariable Integer tipoChequeraId,
                                                     @PathVariable Long chequeraSerieId,
                                                     @PathVariable Integer alternativaId) {
		try {
            return ResponseEntity.ok(service.findLastByChequera(facultadId, tipoChequeraId, chequeraSerieId, alternativaId));
		} catch (DebitoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/cbu/{cbu1}/{cbu2}")
	public ResponseEntity<Debito> findLastActiveByCbu(@PathVariable String cbu1,
                                                      @PathVariable String cbu2) {
		try {
            return ResponseEntity.ok(service.findLastActiveByCbu(cbu1, cbu2));
		} catch (DebitoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/{debitoId}")
	public ResponseEntity<Debito> findByDebitoId(@PathVariable Long debitoId) {
		try {
            return ResponseEntity.ok(service.findByDebitoId(debitoId));
		} catch (DebitoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<Debito> add(@RequestBody Debito debito) {
        return ResponseEntity.ok(service.add(debito));
	}

	@PutMapping("/{debitoId}")
	public ResponseEntity<Debito> update(@RequestBody Debito debito,
                                         @PathVariable Long debitoId) {
        return ResponseEntity.ok(service.add(debito));
	}

}
