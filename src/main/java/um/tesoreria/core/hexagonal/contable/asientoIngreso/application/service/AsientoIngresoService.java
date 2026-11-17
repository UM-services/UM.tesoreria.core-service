package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobDetalle;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResumen;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResultado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in.CancelarJobAsientoIngresoUseCase;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in.ConsultarAsientoIngresoUseCase;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in.ConsultarJobAsientoIngresoUseCase;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in.IniciarRegeneracionMasivaUseCase;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in.RegenerarAsientoIngresoUseCase;

import java.time.OffsetDateTime;

/**
 * Fachada del slice. La regeneración se reintenta una vez cuando la carrera por el alta de
 * {@code ingreso_asiento} choca con la clave única: el segundo intento encuentra la fila y la
 * bloquea. Un error de negocio no se reintenta.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AsientoIngresoService {

    private final ConsultarAsientoIngresoUseCase consultarUseCase;
    private final RegenerarAsientoIngresoUseCase regenerarUseCase;
    private final IniciarRegeneracionMasivaUseCase iniciarMasivaUseCase;
    private final ConsultarJobAsientoIngresoUseCase consultarJobUseCase;
    private final CancelarJobAsientoIngresoUseCase cancelarJobUseCase;

    public AsientoIngresoResumen consultar(OffsetDateTime fecha, Integer tipoPagoId) {
        return consultarUseCase.consultar(fecha, tipoPagoId);
    }

    public AsientoIngresoResultado regenerar(OffsetDateTime fecha, Integer tipoPagoId) {
        try {
            return regenerarUseCase.regenerar(fecha, tipoPagoId);
        } catch (DataIntegrityViolationException | PessimisticLockingFailureException e) {
            log.warn("Asiento ingreso: reintento por concurrencia en {} tipo {}", fecha, tipoPagoId);
            return regenerarUseCase.regenerar(fecha, tipoPagoId);
        }
    }

    public AsientoIngresoJob iniciarMasiva(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta, Long usuarioId) {
        return iniciarMasivaUseCase.iniciar(fechaDesde, fechaHasta, usuarioId);
    }

    public AsientoIngresoJobDetalle consultarJob(Long jobId) {
        return consultarJobUseCase.consultar(jobId);
    }

    public AsientoIngresoJob cancelarJob(Long jobId) {
        return cancelarJobUseCase.cancelar(jobId);
    }

}
