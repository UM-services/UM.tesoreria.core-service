package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.service;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Resolución del mínimo de cotizaciones a partir de los tramos configurados y la referencia del
 * ejercicio.
 *
 * <p>Cada tramo fija un <b>tope</b> expresado como múltiplo de la referencia; el mínimo es la
 * cantidad del primer tramo (por tope ascendente) cuyo tope alcance el monto evaluado. El tramo
 * sin tope ({@code topeMultiplico} nulo, el último) cubre el resto. Sin referencia o sin monto no
 * se resuelve (vacío); sin tramos tampoco.</p>
 */
final class ResolutorMinimoCotizaciones {

    private ResolutorMinimoCotizaciones() {
    }

    static Optional<Integer> resolver(List<CompraCotizacionTramo> tramos, BigDecimal referencia, BigDecimal monto) {
        if (referencia == null || monto == null || tramos == null || tramos.isEmpty()) {
            return Optional.empty();
        }
        return tramos.stream()
                .filter(ResolutorMinimoCotizaciones::esActivo)
                .filter(tramo -> tramo.getCantidadMinima() != null)
                .sorted(Comparator.comparing(ResolutorMinimoCotizaciones::tope,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .filter(tramo -> cubre(tramo, referencia, monto))
                .map(CompraCotizacionTramo::getCantidadMinima)
                .findFirst();
    }

    private static boolean esActivo(CompraCotizacionTramo tramo) {
        return tramo.getActivo() == null || tramo.getActivo() != 0;
    }

    private static Integer tope(CompraCotizacionTramo tramo) {
        return tramo.getTopeMultiplico();
    }

    private static boolean cubre(CompraCotizacionTramo tramo, BigDecimal referencia, BigDecimal monto) {
        if (tramo.esAbierto()) {
            return true;
        }
        BigDecimal limite = referencia.multiply(BigDecimal.valueOf(tramo.getTopeMultiplico()));
        return monto.compareTo(limite) <= 0;
    }
}
