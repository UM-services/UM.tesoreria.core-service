package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import java.util.List;

/**
 * Resultado de la re-acreditación de una importación de Rapipago a partir de un archivo
 * actualizado. No crea importaciones ni pagos: sólo actualiza la acreditación de los pagos que ya
 * existían.
 */
public record ResultadoReacreditacion(
        String filename,
        CanalImportacion canal,
        int registros,
        int actualizados,
        int sinPago,
        int sinRegistro,
        List<String> errores) {
}
