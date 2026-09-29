package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Estado de una chequera: datos del titular, cuotas agrupadas por producto (con sus subtotales
 * oficiales de {@code chequera_total}) y adhesión al débito automático. Es un modelo de lectura
 * que consume {@code report-service} para dibujar el PDF "Estado de Chequera"; este servicio
 * solo arma los datos.
 * <p>
 * Las fechas llegan ya resueltas: {@code primerVencimiento} conserva su día calendario (sin
 * convertir el huso horario) y las fechas de pago, de vencimiento del débito y de envío están en UTC.
 */
public record EstadoChequera(
        Integer facultadId,
        String facultadNombre,
        Integer tipoChequeraId,
        String tipoChequeraNombre,
        Long chequeraSerieId,
        BigDecimal personaId,
        String personaApellido,
        String personaNombre,
        String arancelTipoDescripcion,
        String lectivoNombre,
        BigDecimal becaPorcentaje,
        String tipoImpresionNombre,
        Integer alternativaId,
        List<ProductoEstado> productos,
        List<DebitoEstado> debitos) {
}