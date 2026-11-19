package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTarea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.CuentaFaltante;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.entity.AsientoIngresoJobEntity;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.entity.AsientoIngresoJobTareaEntity;

import java.util.List;

/**
 * Mapea job/tareas y serializa las faltantes. El {@code ObjectMapper} se construye acá: el core no
 * publica un bean de Jackson (varios contextos de test no lo cargan) y no hace falta configuración
 * especial para una lista de records.
 */
@Component
public class AsientoIngresoJobMapper {

    private static final TypeReference<List<CuentaFaltante>> FALTANTES = new TypeReference<>() {
    };

    private static final ObjectMapper JSON = JsonMapper.builder().findAndAddModules().build();

    public AsientoIngresoJobEntity toEntity(AsientoIngresoJob domain) {
        if (domain == null) {
            return null;
        }
        return AsientoIngresoJobEntity.builder()
                .jobId(domain.getJobId())
                .estado(domain.getEstado())
                .fechaDesde(domain.getFechaDesde())
                .fechaHasta(domain.getFechaHasta())
                .totalTareas(domain.getTotalTareas())
                .tareasOk(domain.getTareasOk())
                .tareasError(domain.getTareasError())
                .tareasSinMov(domain.getTareasSinMovimientos())
                .tareasPendientes(domain.getTareasPendientes())
                .usuarioId(domain.getUsuarioId())
                .heartbeat(domain.getHeartbeat())
                .iniciado(domain.getIniciado())
                .finalizado(domain.getFinalizado())
                .mensaje(domain.getMensaje())
                .fechaActual(domain.getFechaActual())
                .tipoPagoActual(domain.getTipoPagoActual())
                .build();
    }

    public AsientoIngresoJob toDomain(AsientoIngresoJobEntity entity) {
        if (entity == null) {
            return null;
        }
        return AsientoIngresoJob.builder()
                .jobId(entity.getJobId())
                .estado(entity.getEstado())
                .fechaDesde(entity.getFechaDesde())
                .fechaHasta(entity.getFechaHasta())
                .totalTareas(entity.getTotalTareas())
                .tareasOk(entity.getTareasOk())
                .tareasError(entity.getTareasError())
                .tareasSinMovimientos(entity.getTareasSinMov())
                .tareasPendientes(entity.getTareasPendientes())
                .usuarioId(entity.getUsuarioId())
                .heartbeat(entity.getHeartbeat())
                .iniciado(entity.getIniciado())
                .finalizado(entity.getFinalizado())
                .mensaje(entity.getMensaje())
                .fechaActual(entity.getFechaActual())
                .tipoPagoActual(entity.getTipoPagoActual())
                .build();
    }

    public AsientoIngresoJobTareaEntity toEntity(AsientoIngresoJobTarea domain) {
        if (domain == null) {
            return null;
        }
        return AsientoIngresoJobTareaEntity.builder()
                .tareaId(domain.getTareaId())
                .jobId(domain.getJobId())
                .fechaContable(domain.getFechaContable())
                .tipoPagoId(domain.getTipoPagoId())
                .estado(domain.getEstado())
                .ordenContable(domain.getOrdenContable())
                .totalDebe(domain.getTotalDebe())
                .totalHaber(domain.getTotalHaber())
                .faltantesJson(toJson(domain.getFaltantes()))
                .errorCodigo(domain.getErrorCodigo())
                .errorMensaje(domain.getErrorMensaje())
                .iniciado(domain.getIniciado())
                .finalizado(domain.getFinalizado())
                .build();
    }

    public AsientoIngresoJobTarea toDomain(AsientoIngresoJobTareaEntity entity) {
        if (entity == null) {
            return null;
        }
        return AsientoIngresoJobTarea.builder()
                .tareaId(entity.getTareaId())
                .jobId(entity.getJobId())
                .fechaContable(entity.getFechaContable())
                .tipoPagoId(entity.getTipoPagoId())
                .estado(entity.getEstado())
                .ordenContable(entity.getOrdenContable())
                .totalDebe(entity.getTotalDebe())
                .totalHaber(entity.getTotalHaber())
                .faltantes(fromJson(entity.getFaltantesJson()))
                .errorCodigo(entity.getErrorCodigo())
                .errorMensaje(entity.getErrorMensaje())
                .iniciado(entity.getIniciado())
                .finalizado(entity.getFinalizado())
                .build();
    }

    private String toJson(List<CuentaFaltante> faltantes) {
        if (faltantes == null || faltantes.isEmpty()) {
            return null;
        }
        try {
            return JSON.writeValueAsString(faltantes);
        } catch (Exception e) {
            return null;
        }
    }

    private List<CuentaFaltante> fromJson(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return JSON.readValue(json, FALTANTES);
        } catch (Exception e) {
            return List.of();
        }
    }

}
