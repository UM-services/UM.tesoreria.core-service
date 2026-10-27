package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Estado de una chequera: datos del titular, cuotas agrupadas por producto (con sus subtotales
 * oficiales de {@code chequera_total}) y adhesión al débito automático. Es un modelo de lectura
 * que consumen {@code report-service} (PDF "Estado de Chequera") y el frontend; este servicio
 * arma los datos y resuelve los cálculos: el {@link EstadoCuota} de cada cuota y la {@code deuda}
 * de cada producto.
 * <p>
 * Las fechas llegan ya resueltas: {@code primerVencimiento} conserva su día calendario (sin
 * convertir el huso horario) y las fechas de pago, de vencimiento del débito y de envío están en UTC.
 * <p>
 * {@code materias} nunca es {@code null}: trae un elemento por fuente consultada y es vacía si la
 * chequera no es de grado.
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
        boolean hpum,
        List<ProductoEstado> productos,
        List<DebitoEstado> debitos,
        List<MateriasEstado> materias) {
}