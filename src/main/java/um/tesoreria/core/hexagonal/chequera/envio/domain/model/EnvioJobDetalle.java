package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Cabecera + tareas, para el detalle que consulta la pantalla con polling. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioJobDetalle {

    private EnvioJob job;
    private List<EnvioJobTarea> tareas;
}
