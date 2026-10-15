package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;

/** Envío del proceso: fija el número reservado y pasa a {@code ENVIADA} con sus documentos. */
public interface EnviarCompraCotizacionUseCase {

    CompraCotizacion enviar(Long compraCotizacionId, String numero);
}
