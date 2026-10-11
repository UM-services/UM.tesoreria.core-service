package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.usecases;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.exception.CompraCotizacionTramoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validación de la configuración de tramos.
 *
 * <p>Los tramos son rangos "hasta un tope": la integridad exige cantidades definidas, topes
 * únicos y a lo sumo un tramo abierto (sin tope). La unicidad del tope se valida también en la
 * base, pero acá se da un mensaje de negocio.</p>
 */
final class CompraCotizacionTramoValidator {

    static final int CANTIDAD_MINIMA_FLOOR = 1;
    static final int CANTIDAD_MINIMA_CEILING = 10;

    private CompraCotizacionTramoValidator() {
    }

    /** Valida los campos de un tramo de forma aislada (admite nulos en edición parcial). */
    static void validarCampos(CompraCotizacionTramo tramo) {
        if (tramo == null) {
            throw new CompraCotizacionTramoException("El tramo de cotizaciones es requerido");
        }
        if (tramo.getTopeMultiplico() != null && tramo.getTopeMultiplico() < 1) {
            throw new CompraCotizacionTramoException(
                    "El tope debe ser mayor o igual a 1 (usar nulo para el tramo sin tope)");
        }
        if (tramo.getCantidadMinima() != null
                && (tramo.getCantidadMinima() < CANTIDAD_MINIMA_FLOOR
                    || tramo.getCantidadMinima() > CANTIDAD_MINIMA_CEILING)) {
            throw new CompraCotizacionTramoException("La cantidad mínima de cotizaciones debe estar entre "
                    + CANTIDAD_MINIMA_FLOOR + " y " + CANTIDAD_MINIMA_CEILING);
        }
    }

    /** Valida el conjunto completo: cantidades definidas, topes únicos y a lo sumo un tramo abierto. */
    static void validarConjunto(List<CompraCotizacionTramo> tramos) {
        if (tramos == null || tramos.isEmpty()) {
            throw new CompraCotizacionTramoException("Debe configurarse al menos un tramo de cotizaciones");
        }
        Set<Integer> topes = new HashSet<>();
        int abiertos = 0;
        for (CompraCotizacionTramo tramo : tramos) {
            validarCampos(tramo);
            if (tramo.getCantidadMinima() == null) {
                throw new CompraCotizacionTramoException("Cada tramo requiere una cantidad mínima de cotizaciones");
            }
            if (tramo.getTopeMultiplico() == null) {
                abiertos++;
            } else if (!topes.add(tramo.getTopeMultiplico())) {
                throw new CompraCotizacionTramoException(
                        "Ya existe un tramo con tope " + tramo.getTopeMultiplico());
            }
        }
        if (abiertos > 1) {
            throw new CompraCotizacionTramoException("Sólo puede haber un tramo sin tope (el último)");
        }
    }
}
