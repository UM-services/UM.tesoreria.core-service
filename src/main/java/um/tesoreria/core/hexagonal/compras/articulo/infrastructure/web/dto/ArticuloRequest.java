package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ArticuloRequest {
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
}
