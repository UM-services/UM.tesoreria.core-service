package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.configuration.security.RequierePermisoInterceptor;
import um.tesoreria.core.hexagonal.chequera.envio.application.service.EnvioService;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.in.EnvioMasivoUseCase;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.ChequeraEnvioAltaResponse;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioJobDetalleResponse;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioJobRequest;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioJobResponse;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioReintentoRequest;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.mapper.ChequeraEnvioAltaDtoMapper;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.mapper.EnvioJobDtoMapper;

import java.util.List;

/**
 * Envío masivo de chequeras por facultad y sede (ex {@code frmCheqMail}).
 *
 * <p>Padrón («Revisar» del legacy) + tres operaciones masivas que corren como job asíncrono:
 * capturar correos, extender débitos y mailing. El envío real lo confirma el sender por
 * {@code notificacion-resultado}.</p>
 */
@RestController
@RequestMapping({"/envio/chequera", "/api/tesoreria/core/envio/chequera"})
@RequiredArgsConstructor
@Slf4j
public class EnvioChequeraController {

    private final EnvioService service;
    private final EnvioMasivoUseCase useCase;
    private final ChequeraEnvioAltaDtoMapper mapper;
    private final EnvioJobDtoMapper jobMapper;

    @GetMapping("/altas/{lectivoId}/{facultadId}/{geograficaId}")
    @RequierePermiso("chequeras.envio.consultar")
    public ResponseEntity<List<ChequeraEnvioAltaResponse>> altas(@PathVariable Integer lectivoId,
                                                                @PathVariable Integer facultadId,
                                                                @PathVariable Integer geograficaId) {
        log.debug("\n\nProcessing EnvioChequeraController.altas {}/{}/{}\n\n",
                lectivoId, facultadId, geograficaId);
        return ResponseEntity.ok(mapper.toResponse(service.altas(lectivoId, facultadId, geograficaId)));
    }

    @PostMapping("/jobs")
    @RequierePermiso("chequeras.envio.ejecutar")
    public ResponseEntity<EnvioJobResponse> iniciar(@RequestBody EnvioJobRequest request, HttpServletRequest http) {
        var job = useCase.iniciar(jobMapper.toFiltro(request), usuarioId(http));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(jobMapper.toResponse(job));
    }

    @GetMapping("/jobs/{jobId}")
    @RequierePermiso("chequeras.envio.consultar")
    public ResponseEntity<EnvioJobDetalleResponse> consultar(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobMapper.toResponse(useCase.consultar(jobId)));
    }

    @PostMapping("/jobs/{jobId}/cancelar")
    @RequierePermiso("chequeras.envio.ejecutar")
    public ResponseEntity<EnvioJobResponse> cancelar(@PathVariable Long jobId) {
        return ResponseEntity.ok(jobMapper.toResponse(useCase.cancelar(jobId)));
    }

    @PostMapping("/jobs/{jobId}/reintentar")
    @RequierePermiso("chequeras.envio.ejecutar")
    public ResponseEntity<EnvioJobResponse> reintentar(@PathVariable Long jobId,
                                                       @RequestBody(required = false) EnvioReintentoRequest request) {
        boolean soloFallidas = request == null || request.isSoloFallidas();
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(jobMapper.toResponse(useCase.reintentar(jobId, soloFallidas)));
    }

    @GetMapping("/jobs/pendientes")
    @RequierePermiso("chequeras.envio.consultar")
    public ResponseEntity<List<EnvioJobResponse>> pendientes() {
        return ResponseEntity.ok(useCase.pendientes().stream().map(jobMapper::toResponse).toList());
    }

    private Long usuarioId(HttpServletRequest request) {
        String header = request.getHeader(RequierePermisoInterceptor.USER_ID_HEADER);
        if (header == null || header.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(header);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
