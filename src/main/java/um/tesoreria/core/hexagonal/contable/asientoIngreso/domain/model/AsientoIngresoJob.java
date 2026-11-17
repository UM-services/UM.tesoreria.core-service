package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/** Job de regeneración masiva (rango de fechas × tipos de pago). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoIngresoJob {

    private Long jobId;
    private AsientoIngresoJobEstado estado;
    private OffsetDateTime fechaDesde;
    private OffsetDateTime fechaHasta;
    private int totalTareas;
    private int tareasOk;
    private int tareasError;
    private int tareasSinMovimientos;
    private int tareasPendientes;
    private Long usuarioId;
    private OffsetDateTime heartbeat;
    private OffsetDateTime iniciado;
    private OffsetDateTime finalizado;
    private String mensaje;
    private OffsetDateTime fechaActual;
    private Integer tipoPagoActual;

    public double porcentaje() {
        if (totalTareas == 0) {
            return 0;
        }
        int resueltas = tareasOk + tareasError + tareasSinMovimientos;
        return Math.round(resueltas * 1000.0 / totalTareas) / 10.0;
    }

    public boolean finalizado() {
        return estado == AsientoIngresoJobEstado.COMPLETADO
                || estado == AsientoIngresoJobEstado.CANCELADO
                || estado == AsientoIngresoJobEstado.ERROR;
    }

}
