package um.tesoreria.core.hexagonal.contable.asientoManual.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableLinea;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableResultado;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;

public interface AsientoManualUseCase {

    AsientoContableResultado registrar(OffsetDateTime fecha, Integer numero, String concepto,
                                       List<AsientoContableLinea> lineas);

    AsientoContableResultado consultar(OffsetDateTime fecha, Integer orden);

    void eliminar(OffsetDateTime fecha, Integer orden);

    List<AsientoContableLinea> importar(InputStream archivo);

}
