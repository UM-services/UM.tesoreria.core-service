package um.tesoreria.core.hexagonal.articulo.domain.model;

import lombok.*;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
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
}
