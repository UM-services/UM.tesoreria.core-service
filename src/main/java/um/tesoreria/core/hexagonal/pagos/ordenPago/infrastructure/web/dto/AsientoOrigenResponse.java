package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

/** Origen de un asiento propuesto: el documento (factura, entrega o valor) al que responde. */
public record AsientoOrigenResponse(String tipo, Long referenciaId) {
}
