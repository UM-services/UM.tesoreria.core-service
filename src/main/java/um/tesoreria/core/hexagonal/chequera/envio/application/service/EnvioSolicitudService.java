package um.tesoreria.core.hexagonal.chequera.envio.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.envio.application.exception.EnvioException;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitud;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudClaim;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudCreacion;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioSolicitudEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioTareaEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.in.EnvioSolicitudUseCase;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.EnvioSolicitudRepository;
import um.tesoreria.core.model.ChequeraMessageCheck;
import um.tesoreria.core.service.ChequeraMessageCheckService;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Ledger idempotente del envío de chequeras.
 *
 * <p>Una solicitud por {@code chequera_id} (clave de la serie). El sender la reclama antes de
 * generar el PDF y mandar el correo; si ya está ENVIADA, ENVIANDO con lock vigente o agotó los
 * intentos, no se manda nada. Así una reentrega de Kafka, un doble clic o un job repetido son
 * no-ops.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EnvioSolicitudService implements EnvioSolicitudUseCase {

    private static final String PREFIJO_DEDUPE = "CHEQUERA:";

    private final EnvioSolicitudRepository repository;
    private final ChequeraSerieService chequeraSerieService;
    private final ChequeraMessageCheckService chequeraMessageCheckService;
    private final EnvioJobService envioJobService;

    @Value("${tesoreria.chequeras-envio.solicitud.max-intentos:3}")
    private int maxIntentos;

    @Value("${tesoreria.chequeras-envio.solicitud.lock-seconds:120}")
    private long lockSeconds;

    @Override
    @Transactional
    public EnvioSolicitudCreacion crear(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                        Long jobId, String origen) {
        ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        return crear(serie, jobId, origen);
    }

    /** Variante para quien ya tiene la serie leída (envío individual y job). */
    @Transactional
    public EnvioSolicitudCreacion crear(ChequeraSerie serie, Long jobId, String origen) {
        var existente = repository.findByChequeraId(serie.getChequeraId());
        if (existente.isPresent()) {
            EnvioSolicitud solicitud = existente.get();
            return new EnvioSolicitudCreacion(solicitud, puedePublicar(solicitud));
        }

        boolean yaEnviada = serie.getEnviado() != null && serie.getEnviado() != 0;
        EnvioSolicitud solicitud = EnvioSolicitud.builder()
                .solicitudId(UUID.randomUUID())
                .chequeraId(serie.getChequeraId())
                .facultadId(serie.getFacultadId())
                .tipoChequeraId(serie.getTipoChequeraId())
                .chequeraSerieId(serie.getChequeraSerieId())
                .lectivoId(serie.getLectivoId())
                .jobId(jobId)
                .origen(origen == null || origen.isBlank() ? "MANUAL" : origen)
                .dedupeKey(PREFIJO_DEDUPE + serie.getChequeraId())
                .estado(yaEnviada ? EnvioSolicitudEstado.ENVIADO : EnvioSolicitudEstado.PENDIENTE)
                .intentos(0)
                .enviadoAt(yaEnviada ? ahora() : null)
                .build();
        EnvioSolicitud creada = repository.save(solicitud);
        log.info("Envío chequera: solicitud {} creada para chequera {} en estado {}",
                creada.getSolicitudId(), creada.getChequeraId(), creada.getEstado());
        return new EnvioSolicitudCreacion(creada, !yaEnviada);
    }

    @Override
    @Transactional
    public EnvioSolicitudClaim reclamar(UUID solicitudId) {
        EnvioSolicitud solicitud = buscarParaActualizar(solicitudId);
        OffsetDateTime ahora = ahora();

        if (solicitud.getEstado() == EnvioSolicitudEstado.ENVIADO) {
            return new EnvioSolicitudClaim(solicitud, false, "YA_ENVIADA");
        }
        if (solicitud.getEstado() == EnvioSolicitudEstado.ENVIANDO
                && solicitud.getLockHasta() != null
                && solicitud.getLockHasta().isAfter(ahora)) {
            return new EnvioSolicitudClaim(solicitud, false, "EN_CURSO");
        }
        if (solicitud.getIntentos() >= maxIntentos) {
            return new EnvioSolicitudClaim(solicitud, false, "MAX_INTENTOS");
        }

        solicitud.setEstado(EnvioSolicitudEstado.ENVIANDO);
        solicitud.setIntentos(solicitud.getIntentos() + 1);
        solicitud.setLockHasta(ahora.plusSeconds(lockSeconds));
        solicitud.setUltimoError(null);
        EnvioSolicitud reclamada = repository.save(solicitud);
        log.info("Envío chequera: solicitud {} reclamada (intento {})", solicitudId, reclamada.getIntentos());
        return new EnvioSolicitudClaim(reclamada, true, null);
    }

    @Override
    @Transactional
    public EnvioSolicitud marcarEnviado(UUID solicitudId) {
        EnvioSolicitud solicitud = buscarParaActualizar(solicitudId);
        if (solicitud.getEstado() == EnvioSolicitudEstado.ENVIADO) {
            return solicitud;
        }

        solicitud.setEstado(EnvioSolicitudEstado.ENVIADO);
        solicitud.setEnviadoAt(ahora());
        solicitud.setLockHasta(null);
        solicitud.setUltimoError(null);
        EnvioSolicitud enviada = repository.save(solicitud);

        chequeraSerieService.markSent(solicitud.getFacultadId(), solicitud.getTipoChequeraId(),
                solicitud.getChequeraSerieId());
        chequeraMessageCheckService.add(ChequeraMessageCheck.builder()
                .chequeraMessageCheckId(UUID.randomUUID())
                .facultadId(solicitud.getFacultadId())
                .tipoChequeraId(solicitud.getTipoChequeraId())
                .chequeraSerieId(solicitud.getChequeraSerieId())
                .build());
        if (solicitud.getJobId() != null) {
            envioJobService.registrarAcuse(solicitud.getJobId(), solicitud.getChequeraId(),
                    EnvioTareaEstado.ENVIADA, true, null);
        }
        log.info("Envío chequera: solicitud {} confirmada como ENVIADA (chequera {})",
                solicitudId, solicitud.getChequeraId());
        return enviada;
    }

    @Override
    @Transactional
    public EnvioSolicitud marcarFallido(UUID solicitudId, String error) {
        EnvioSolicitud solicitud = buscarParaActualizar(solicitudId);
        if (solicitud.getEstado() == EnvioSolicitudEstado.ENVIADO) {
            return solicitud;
        }

        String mensaje = error == null || error.isBlank() ? "Error de envío" : error;
        solicitud.setEstado(EnvioSolicitudEstado.FALLIDO);
        solicitud.setUltimoError(recortar(mensaje));
        solicitud.setLockHasta(null);
        EnvioSolicitud fallida = repository.save(solicitud);

        if (solicitud.getJobId() != null && solicitud.getIntentos() >= maxIntentos) {
            envioJobService.registrarAcuse(solicitud.getJobId(), solicitud.getChequeraId(),
                    EnvioTareaEstado.ERROR_SMTP, false, mensaje);
        }
        log.warn("Envío chequera: solicitud {} falló (intento {}): {}", solicitudId,
                solicitud.getIntentos(), mensaje);
        return fallida;
    }

    @Override
    @Transactional
    public EnvioSolicitud marcarOmitida(UUID solicitudId, EnvioSolicitudEstado estado, String mensaje) {
        if (estado == null || !esOmitida(estado)) {
            throw new EnvioException("ENVIO_ESTADO_INVALIDO", "Estado de omisión inválido: " + estado);
        }
        EnvioSolicitud solicitud = buscarParaActualizar(solicitudId);
        if (solicitud.getEstado() == EnvioSolicitudEstado.ENVIADO) {
            return solicitud;
        }

        solicitud.setEstado(estado);
        solicitud.setUltimoError(recortar(mensaje));
        solicitud.setLockHasta(null);
        EnvioSolicitud omitida = repository.save(solicitud);

        if (solicitud.getJobId() != null) {
            envioJobService.registrarAcuse(solicitud.getJobId(), solicitud.getChequeraId(),
                    estadoTarea(estado), false, mensaje);
        }
        log.info("Envío chequera: solicitud {} omitida ({})", solicitudId, estado);
        return omitida;
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioSolicitud consultar(UUID solicitudId) {
        return repository.findBySolicitudId(solicitudId)
                .orElseThrow(() -> new EnvioException("ENVIO_SOLICITUD_INEXISTENTE",
                        "No existe la solicitud " + solicitudId));
    }

    private EnvioSolicitud buscarParaActualizar(UUID solicitudId) {
        return repository.findBySolicitudIdForUpdate(solicitudId)
                .orElseThrow(() -> new EnvioException("ENVIO_SOLICITUD_INEXISTENTE",
                        "No existe la solicitud " + solicitudId));
    }

    /** Un evento sólo se vuelve a publicar si la solicitud todavía puede enviarse. */
    private boolean puedePublicar(EnvioSolicitud solicitud) {
        if (solicitud.getEstado() == EnvioSolicitudEstado.ENVIADO) {
            return false;
        }
        if (solicitud.getIntentos() >= maxIntentos) {
            return false;
        }
        if (solicitud.getEstado() == EnvioSolicitudEstado.ENVIANDO) {
            return solicitud.getLockHasta() == null || solicitud.getLockHasta().isBefore(ahora());
        }
        return true;
    }

    private static boolean esOmitida(EnvioSolicitudEstado estado) {
        return estado == EnvioSolicitudEstado.OMITIDA
                || estado == EnvioSolicitudEstado.SIN_CORREOS
                || estado == EnvioSolicitudEstado.SIN_CUOTAS
                || estado == EnvioSolicitudEstado.RETENIDA;
    }

    private static EnvioTareaEstado estadoTarea(EnvioSolicitudEstado estado) {
        return switch (estado) {
            case SIN_CUOTAS -> EnvioTareaEstado.ERROR_SMTP;
            case SIN_CORREOS -> EnvioTareaEstado.SIN_CORREOS;
            case RETENIDA -> EnvioTareaEstado.RETENIDA;
            default -> EnvioTareaEstado.OMITIDA;
        };
    }

    private static String recortar(String texto) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= 500 ? texto : texto.substring(0, 500);
    }

    private static OffsetDateTime ahora() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

}
