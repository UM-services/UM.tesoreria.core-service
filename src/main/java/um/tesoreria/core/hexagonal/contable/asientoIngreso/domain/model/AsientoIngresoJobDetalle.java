package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Estado completo de un job: cabecera, progreso por día y errores. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoIngresoJobDetalle {

    private AsientoIngresoJob job;
    private List<AsientoIngresoJobResumenDia> resumenPorFecha;
    private List<AsientoIngresoJobError> errores;

}
