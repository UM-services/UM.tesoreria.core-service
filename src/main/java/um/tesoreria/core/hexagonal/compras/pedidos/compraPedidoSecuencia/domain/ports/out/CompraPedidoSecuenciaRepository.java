package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.domain.ports.out;

/**
 * Puerto de salida del correlativo anual del pedido de compra.
 *
 * <p>La reserva debe ser atómica: dos altas concurrentes del mismo ejercicio no pueden
 * obtener el mismo número.</p>
 */
public interface CompraPedidoSecuenciaRepository {

    /**
     * Reserva y devuelve el siguiente correlativo del ejercicio. Si es la primera reserva
     * del ejercicio, arranca en {@code 1}.
     */
    int reservarSiguienteNumero(Integer ejercicioId, Integer anio);

}
