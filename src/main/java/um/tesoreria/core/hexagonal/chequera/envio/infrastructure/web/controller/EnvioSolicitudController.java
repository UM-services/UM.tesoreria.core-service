package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.hexagonal.chequera.envio.application.exception.EnvioException;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.in.EnvioSolicitudUseCase;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioSolicitudClaimResponse;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioSolicitudFalloRequest;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioSolicitudOmisionRequest;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioSolicitudRequest;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto.EnvioSolicitudResponse;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.mapper.EnvioSolicitudDtoMapper;

import java.util.UUID;

/**
 * Ledger idempotente del envío de chequeras. Lo usan el sender (claim/sent/failed/omitida) y el
 * propio core para crear la solicitud antes de publicar en {@code send-chequera}.
 *
 * <p>Son endpoints internos de servicio: no llevan {@code @RequierePermiso} porque los consume
 * {@code tesoreria-sender} por red interna, igual que {@code chequeraSerie/mark/sent}.</p>
 */
@RestController
@RequestMapping({"/envio/chequera/solicitud", "/api/tesoreria/core/envio/chequera/solicitud"})
@RequiredArgsConstructor
@Slf4j
public class EnvioSolicitudController {

    private final EnvioSolicitudUseCase useCase;
    private final EnvioSolicitudDtoMapper mapper;

    @PostMapping
    public ResponseEntity<EnvioSolicitudResponse> crear(@RequestBody EnvioSolicitudRequest request) {
        log.debug("Envío chequera: crear solicitud {}/{}/{}", request.facultadId(),
                request.tipoChequeraId(), request.chequeraSerieId());
        var creacion = useCase.crear(request.facultadId(), request.tipoChequeraId(),
                request.chequeraSerieId(), request.jobId(), request.origen());
        return ResponseEntity.ok(mapper.toResponse(creacion.solicitud()));
    }

    @PostMapping("/{solicitudId}/claim")
    public ResponseEntity<EnvioSolicitudClaimResponse> reclamar(@PathVariable UUID solicitudId) {
        return ResponseEntity.ok(mapper.toResponse(useCase.reclamar(solicitudId)));
    }

    @PostMapping("/{solicitudId}/sent")
    public ResponseEntity<Void> marcarEnviado(@PathVariable UUID solicitudId) {
        useCase.marcarEnviado(solicitudId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{solicitudId}/failed")
    public ResponseEntity<EnvioSolicitudResponse> marcarFallido(@PathVariable UUID solicitudId,
                                                                @RequestBody(required = false) EnvioSolicitudFalloRequest request) {
        String error = request == null ? null : request.error();
        return ResponseEntity.ok(mapper.toResponse(useCase.marcarFallido(solicitudId, error)));
    }

    @PostMapping("/{solicitudId}/omitida")
    public ResponseEntity<Void> marcarOmitida(@PathVariable UUID solicitudId,
                                              @RequestBody EnvioSolicitudOmisionRequest request) {
        useCase.marcarOmitida(solicitudId, parseEstado(request.estado()), request.mensaje());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{solicitudId}")
    public ResponseEntity<EnvioSolicitudResponse> consultar(@PathVariable UUID solicitudId) {
        return ResponseEntity.ok(mapper.toResponse(useCase.consultar(solicitudId)));
    }

    private static EnvioSolicitudEstado parseEstado(String estado) {
        try {
            return EnvioSolicitudEstado.valueOf(estado);
        } catch (RuntimeException e) {
            throw new EnvioException("ENVIO_ESTADO_INVALIDO", "Estado de omisión inválido: " + estado);
        }
    }

}
