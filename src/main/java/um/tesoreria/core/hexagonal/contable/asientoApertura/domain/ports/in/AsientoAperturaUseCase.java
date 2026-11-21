package um.tesoreria.core.hexagonal.contable.asientoApertura.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableLinea;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableResultado;

import java.util.List;

public interface AsientoAperturaUseCase {

    AsientoContableResultado preview(Integer ejercicioId, Integer referencia);

    AsientoContableResultado registrar(Integer ejercicioId, String concepto, List<AsientoContableLinea> lineas);

}
