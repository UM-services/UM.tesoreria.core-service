package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Pedido de previsualización o de importación de un archivo.
 *
 * @param lineasAceptadas números de línea aceptados por el operador; {@code null} significa
 *                        "todas las que estén en estado OK"
 * @param desde           inicio del período para los canales que no usan archivo (PayPerTIC)
 * @param hasta           fin del período para los canales que no usan archivo (PayPerTIC)
 */
public record SolicitudImportacion(
        CanalImportacion canal,
        String filename,
        byte[] contenido,
        List<Integer> lineasAceptadas,
        boolean permitirDuplicados,
        OffsetDateTime desde,
        OffsetDateTime hasta) {

    public static SolicitudImportacion deArchivo(CanalImportacion canal, String filename, byte[] contenido,
                                                 List<Integer> lineasAceptadas, boolean permitirDuplicados) {
        return new SolicitudImportacion(canal, filename, contenido, lineasAceptadas, permitirDuplicados, null, null);
    }

    public static SolicitudImportacion dePeriodo(CanalImportacion canal, String filename,
                                                 OffsetDateTime desde, OffsetDateTime hasta,
                                                 List<Integer> lineasAceptadas, boolean permitirDuplicados) {
        return new SolicitudImportacion(canal, filename, null, lineasAceptadas, permitirDuplicados, desde, hasta);
    }

}
