package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out;

/** Reserva del correlativo anual de recepciones ({@code RC-AAAA-NNNNNN}). */
public interface CompraRecepcionSecuenciaPort {

    int reservarNumero(Integer ejercicioId, Integer anio);

}
