package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.EjercicioActual;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Puerto de salida para resolver el ejercicio contable vigente. El pedido lo necesita al
 * crearse para cumplir la FK {@code compra_pedido.ejercicio_id}.
 */
public interface EjercicioActualPort {

    Optional<EjercicioActual> findEjercicioActual(LocalDateTime fecha);

}
