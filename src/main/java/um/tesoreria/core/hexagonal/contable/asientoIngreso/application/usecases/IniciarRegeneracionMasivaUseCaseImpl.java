package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.usecases;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException.Codigo;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service.FechasContables;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTarea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTareaEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in.IniciarRegeneracionMasivaUseCase;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.AsientoIngresoJobRepository;
import um.tesoreria.core.kotlin.model.TipoPago;
import um.tesoreria.core.service.TipoPagoService;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
public class IniciarRegeneracionMasivaUseCaseImpl implements IniciarRegeneracionMasivaUseCase {

    private final AsientoIngresoJobRepository repository;
    private final TipoPagoService tipoPagoService;
    private final TransactionOperations transaction;
    private final int maxDias;

    public IniciarRegeneracionMasivaUseCaseImpl(AsientoIngresoJobRepository repository,
                                                TipoPagoService tipoPagoService,
                                                TransactionOperations transaction,
                                                @Value("${tesoreria.asiento-ingreso.masivo.max-dias:366}") int maxDias) {
        this.repository = repository;
        this.tipoPagoService = tipoPagoService;
        this.transaction = transaction;
        this.maxDias = maxDias;
    }

    @Override
    public AsientoIngresoJob iniciar(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta, Long usuarioId) {
        OffsetDateTime desde = FechasContables.normalizar(fechaDesde);
        OffsetDateTime hasta = FechasContables.normalizar(fechaHasta);

        if (desde.isAfter(hasta)) {
            throw new AsientoIngresoException(Codigo.RANGO_INVALIDO,
                    "La fecha desde no puede ser posterior a la fecha hasta");
        }
        long dias = FechasContables.diasEntre(desde, hasta);
        if (dias > maxDias) {
            throw new AsientoIngresoException(Codigo.RANGO_EXCEDIDO,
                    "El rango supera el máximo permitido de " + maxDias + " días");
        }

        List<TipoPago> tipos = tipoPagoService.findAll().stream()
                .filter(tipo -> tipo.getTipoPagoId() != null)
                .sorted(Comparator.comparing(TipoPago::getTipoPagoId, Comparator.nullsLast(Integer::compareTo)))
                .toList();

        List<AsientoIngresoJobTarea> tareas = new ArrayList<>();
        for (OffsetDateTime fecha = desde; !fecha.isAfter(hasta); fecha = fecha.plusDays(1)) {
            for (TipoPago tipo : tipos) {
                tareas.add(AsientoIngresoJobTarea.builder()
                        .fechaContable(fecha)
                        .tipoPagoId(tipo.getTipoPagoId())
                        .estado(AsientoIngresoJobTareaEstado.PENDIENTE)
                        .build());
            }
        }

        OffsetDateTime ahora = OffsetDateTime.now(ZoneOffset.UTC);
        AsientoIngresoJob job = AsientoIngresoJob.builder()
                .estado(tareas.isEmpty() ? AsientoIngresoJobEstado.COMPLETADO : AsientoIngresoJobEstado.PENDIENTE)
                .fechaDesde(desde)
                .fechaHasta(hasta)
                .totalTareas(tareas.size())
                .tareasPendientes(tareas.size())
                .usuarioId(usuarioId)
                .iniciado(tareas.isEmpty() ? ahora : null)
                .finalizado(tareas.isEmpty() ? ahora : null)
                .build();

        return Objects.requireNonNull(transaction.execute(status -> repository.create(job, tareas)));
    }

}
