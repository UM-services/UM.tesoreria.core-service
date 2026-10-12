package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out;

/** Reserva del correlativo anual de Órdenes de Compra ({@code OC-AAAA-NNNNNN}). */
public interface CompraFormalizacionSecuenciaPort {

    int reservarNumero(Integer ejercicioId, Integer anio);
}
