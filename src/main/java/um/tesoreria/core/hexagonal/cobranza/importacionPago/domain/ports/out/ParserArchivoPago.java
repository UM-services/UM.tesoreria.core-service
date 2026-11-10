package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out;

import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.CanalImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.LineaImportacion;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Parser de un formato de rendición. Cada implementación vive en
 * {@code infrastructure/parser} y sólo traduce bytes a {@link LineaImportacion}; no resuelve
 * chequeras ni escribe en la base.
 */
public interface ParserArchivoPago {

    CanalImportacion canal();

    List<LineaImportacion> parsear(String filename, byte[] contenido);

    /**
     * {@code true} para los canales que se importan por período en lugar de por archivo
     * (PayPerTIC). El parser debe implementar {@link #porPeriodo(OffsetDateTime, OffsetDateTime)}.
     */
    default boolean soportaPeriodo() {
        return false;
    }

    default List<LineaImportacion> porPeriodo(OffsetDateTime desde, OffsetDateTime hasta) {
        throw new UnsupportedOperationException("El canal " + canal() + " no se importa por período");
    }

}
