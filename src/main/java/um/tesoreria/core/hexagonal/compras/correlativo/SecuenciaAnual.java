package um.tesoreria.core.hexagonal.compras.correlativo;

/**
 * Fila de correlativo anual de compras ({@code compra_*_secuencia}): una por ejercicio, con el
 * último número entregado. Las cuatro tablas comparten la misma forma.
 */
public interface SecuenciaAnual {

    Integer getEjercicioId();

    Integer getUltimoNumero();

    void setUltimoNumero(Integer ultimoNumero);

}
