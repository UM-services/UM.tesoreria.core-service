package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException.Codigo;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service.AsientoIngresoCatalogo;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobDetalle;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobError;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobResumenDia;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTarea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTareaEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in.ConsultarJobAsientoIngresoUseCase;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.AsientoIngresoJobRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
public class ConsultarJobAsientoIngresoUseCaseImpl implements ConsultarJobAsientoIngresoUseCase {

    private final AsientoIngresoJobRepository repository;
    private final AsientoIngresoCatalogo catalogo;

    @Override
    public AsientoIngresoJobDetalle consultar(Long jobId) {
        AsientoIngresoJob job = repository.findByJobId(jobId)
                .orElseThrow(() -> new AsientoIngresoException(Codigo.JOB_NO_ENCONTRADO,
                        "No existe el job " + jobId));

        List<AsientoIngresoJobTarea> tareas = repository.findAllTareasByJobId(jobId);

        Map<OffsetDateTime, List<AsientoIngresoJobTarea>> porFecha = new TreeMap<>();
        tareas.forEach(tarea -> porFecha.computeIfAbsent(tarea.getFechaContable(), k -> new java.util.ArrayList<>())
                .add(tarea));

        List<AsientoIngresoJobResumenDia> resumen = porFecha.entrySet().stream()
                .map(entry -> AsientoIngresoJobResumenDia.builder()
                        .fecha(entry.getKey())
                        .ok((int) entry.getValue().stream()
                                .filter(t -> t.getEstado() == AsientoIngresoJobTareaEstado.OK).count())
                        .error((int) entry.getValue().stream()
                                .filter(t -> t.getEstado() == AsientoIngresoJobTareaEstado.ERROR).count())
                        .sinMovimientos((int) entry.getValue().stream()
                                .filter(t -> t.getEstado() == AsientoIngresoJobTareaEstado.SIN_MOVIMIENTOS).count())
                        .build())
                .toList();

        List<AsientoIngresoJobError> errores = tareas.stream()
                .filter(tarea -> tarea.getEstado() == AsientoIngresoJobTareaEstado.ERROR)
                .map(tarea -> AsientoIngresoJobError.builder()
                        .fecha(tarea.getFechaContable())
                        .tipoPagoId(tarea.getTipoPagoId())
                        .tipoPagoNombre(catalogo.tipoPagoNombre(tarea.getTipoPagoId()))
                        .codigo(tarea.getErrorCodigo())
                        .mensaje(tarea.getErrorMensaje())
                        .build())
                .toList();

        return AsientoIngresoJobDetalle.builder()
                .job(job)
                .resumenPorFecha(resumen)
                .errores(errores)
                .build();
    }

}
