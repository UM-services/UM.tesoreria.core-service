package um.tesoreria.core.hexagonal.compras.articulo.domain.model;

import lombok.*;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Articulo {
    private Long articuloId;
    private String nombre;
    private String descripcion;
    private String unidad;
    private BigDecimal precio;
    private Byte inventariable;
    private Long stockMinimo;
    private BigDecimal numeroCuenta;
    private String tipo;
    private Byte directo;
    private Byte habilitado;
    private Cuenta cuenta;

    /**
     * Estado resultante de aplicar una edición: cada campo nulo en {@code cambios} conserva el valor actual.
     * El id no cambia y {@code cuenta} no se copia (puede no corresponder al {@code numeroCuenta} nuevo).
     */
    public Articulo conCambios(Articulo cambios) {
        return Articulo.builder()
                .articuloId(articuloId)
                .nombre(cambios.nombre != null ? cambios.nombre : nombre)
                .descripcion(cambios.descripcion != null ? cambios.descripcion : descripcion)
                .unidad(cambios.unidad != null ? cambios.unidad : unidad)
                .precio(cambios.precio != null ? cambios.precio : precio)
                .inventariable(cambios.inventariable != null ? cambios.inventariable : inventariable)
                .stockMinimo(cambios.stockMinimo != null ? cambios.stockMinimo : stockMinimo)
                .numeroCuenta(cambios.numeroCuenta != null ? cambios.numeroCuenta : numeroCuenta)
                .tipo(cambios.tipo != null ? cambios.tipo : tipo)
                .directo(cambios.directo != null ? cambios.directo : directo)
                .habilitado(cambios.habilitado != null ? cambios.habilitado : habilitado)
                .build();
    }
}
