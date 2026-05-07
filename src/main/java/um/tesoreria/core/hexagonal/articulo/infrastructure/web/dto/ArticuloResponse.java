package um.tesoreria.core.hexagonal.articulo.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloResponse {
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
