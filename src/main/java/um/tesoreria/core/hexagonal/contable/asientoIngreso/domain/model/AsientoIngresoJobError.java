package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/** Error de una tarea, listo para mostrar en la pantalla. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoIngresoJobError {

    private OffsetDateTime fecha;
    private Integer tipoPagoId;
    private String tipoPagoNombre;
    private String codigo;
    private String mensaje;

}
