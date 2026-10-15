package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.service;

import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionItem;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cálculo del saldo contratado de la recepción (puro, sin framework).
 *
 * <p>Una entrega cubre el saldo sólo si su resultado lo permite (D-REC-10): lo {@code NO_CONFORME}
 * se registra para trazabilidad pero queda pendiente de reposición. El saldo nunca es negativo
 * (la aplicación no admite recibir más que el saldo, D-REC-13).</p>
 */
final class CalculadoraSaldosRecepcion {

    private CalculadoraSaldosRecepcion() {
    }

    /** Cantidad cubierta por ítem de la OC, sumando las entregas registradas. */
    static Map<Long, BigDecimal> cubiertoPorItem(List<CompraRecepcion> recepciones) {
        Map<Long, BigDecimal> cubierto = new LinkedHashMap<>();
        if (recepciones == null) {
            return cubierto;
        }
        for (CompraRecepcion recepcion : recepciones) {
            if (recepcion == null || recepcion.getItems() == null) {
                continue;
            }
            for (CompraRecepcionItem item : recepcion.getItems()) {
                if (item.getCompraFormalizacionItemId() == null
                        || item.getCantidadRecibida() == null
                        || item.getResultado() == null
                        || !item.getResultado().cubreSaldo()) {
                    continue;
                }
                cubierto.merge(item.getCompraFormalizacionItemId(), item.getCantidadRecibida(), BigDecimal::add);
            }
        }
        return cubierto;
    }

    /** Saldo del ítem: contratado − cubierto, acotado a cero. */
    static BigDecimal saldo(BigDecimal contratado, BigDecimal cubierto) {
        BigDecimal base = contratado == null ? BigDecimal.ZERO : contratado;
        BigDecimal recibido = cubierto == null ? BigDecimal.ZERO : cubierto;
        BigDecimal saldo = base.subtract(recibido);
        return saldo.signum() < 0 ? BigDecimal.ZERO : saldo;
    }

}
