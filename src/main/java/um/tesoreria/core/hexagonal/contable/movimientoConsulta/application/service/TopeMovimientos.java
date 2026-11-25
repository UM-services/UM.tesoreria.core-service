package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Topes de una consulta de movimientos por rango: líneas ({@code app.contabilidad.consulta.max-lineas}) y días
 * ({@code app.contabilidad.consulta.max-dias}). El tope de días evita contar toda la historia en cada pedido.
 */
@Component
public class TopeMovimientos {

    private final int maximo;
    private final int maximoDias;

    public TopeMovimientos(@Value("${app.contabilidad.consulta.max-lineas:100000}") int maximo,
                           @Value("${app.contabilidad.consulta.max-dias:400}") int maximoDias) {
        // La lectura pide maximo + 1 líneas: con 0 o con Integer.MAX_VALUE todos los pedidos fallarían.
        if (maximo < 1 || maximo == Integer.MAX_VALUE) {
            throw new IllegalArgumentException(
                    "app.contabilidad.consulta.max-lineas debe estar entre 1 y " + (Integer.MAX_VALUE - 1) + ": " + maximo);
        }
        if (maximoDias < 1) {
            throw new IllegalArgumentException("app.contabilidad.consulta.max-dias debe ser mayor que 0: " + maximoDias);
        }
        this.maximo = maximo;
        this.maximoDias = maximoDias;
    }

    public int maximo() {
        return maximo;
    }

    public int maximoDias() {
        return maximoDias;
    }
}
