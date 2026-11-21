package um.tesoreria.core.hexagonal.contable.asientoCierre.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableLinea;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableResultado;
import um.tesoreria.core.hexagonal.contable.asientoCierre.domain.model.VarianteCierre;

import java.util.List;

public interface AsientoCierreUseCase {

    AsientoContableResultado preview(Integer ejercicioId, VarianteCierre variante);

    AsientoContableResultado registrar(Integer ejercicioId, VarianteCierre variante, String concepto,
                                       List<AsientoContableLinea> lineas);

}
