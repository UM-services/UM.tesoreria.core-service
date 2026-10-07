package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.domain.ports.in;

public interface ReservarNumeroCompraPedidoUseCase {

    int reservarNumero(Integer ejercicioId, Integer anio);

}
