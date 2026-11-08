package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out;

import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoGeneracion;

import java.time.OffsetDateTime;

/**
 * Generación de PayPerTIC. Se delega en el servicio existente hasta completar el traslado al
 * slice.
 */
public interface PayPerTicFileGenerator {

    ResultadoGeneracion generar(OffsetDateTime desde, OffsetDateTime hasta);

}
