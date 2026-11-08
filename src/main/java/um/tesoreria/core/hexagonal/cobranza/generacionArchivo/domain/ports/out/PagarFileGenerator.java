package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out;

import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoGeneracion;

import java.time.OffsetDateTime;

/**
 * Generación de PAGAR. Se delega en el servicio existente hasta completar el traslado al slice.
 */
public interface PagarFileGenerator {

    ResultadoGeneracion generar(OffsetDateTime desde, OffsetDateTime hasta);

}
