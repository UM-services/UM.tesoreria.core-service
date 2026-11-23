package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.ClaveImputacion;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.ClaveSerie;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * Lecturas de referencia de la regeneración, resueltas en bloque para no pagar un N+1 por pago
 * (un día con 500 pagos hacía 500 búsquedas de serie y 500 de imputación).
 */
public interface AsientoIngresoReferenciaRepository {

    /** {@code chequera_serie.ChS_Lec_ID} por clave. Omite las series inexistentes. */
    Map<ClaveSerie, Integer> findLectivosBySeries(Collection<ClaveSerie> claves);

    /** {@code chequera_serie_reemplazo.CSR_Lec_ID} por clave. Omite las series inexistentes. */
    Map<ClaveSerie, Integer> findLectivosReemplazoBySeries(Collection<ClaveSerie> claves);

    /**
     * Cuenta de la imputación por clave. Omite las claves sin cuenta (el asiento las informa como
     * cuentas faltantes).
     */
    Map<ClaveImputacion, BigDecimal> findCuentasByFacultades(Collection<Integer> facultades);

}
