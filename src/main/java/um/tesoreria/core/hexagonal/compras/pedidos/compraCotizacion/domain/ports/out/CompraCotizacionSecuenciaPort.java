package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out;

/** Reserva del correlativo anual de procesos de cotización ({@code SC-AAAA-NNNNNN}). */
public interface CompraCotizacionSecuenciaPort {

    int reservarNumero(Integer ejercicioId, Integer anio);
}
