package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** Resumen + faltantes + acción ejecutada por una regeneración. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoIngresoResultado {

    private AsientoIngresoResumen resumen;
    private AsientoIngresoAccion accion;
    private List<CuentaFaltante> faltantes;

}
