package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ArticuloRequest {
    @Schema(description = "Obligatorio en el alta (1 a 2147483647); en el PUT se ignora el del cuerpo", example = "465")
    private Long articuloId;
    @Schema(description = "Hasta 150 caracteres; vacío se acepta", example = "Fotocopias")
    private String nombre;
    @Schema(description = "Hasta 64 caracteres")
    private String descripcion;
    @Schema(description = "Hasta 16 caracteres")
    private String unidad;
    @Schema(description = "Hasta 14 dígitos enteros; los decimales de más se redondean a 2", example = "10.50")
    private BigDecimal precio;
    @Schema(description = "0 o 1")
    private Byte inventariable;
    private Long stockMinimo;
    @Schema(description = "Entero de hasta 11 dígitos que exista en el plan de cuentas; nulo = sin cuenta en el alta, sin cambios en el PUT")
    private BigDecimal numeroCuenta;
    @Schema(description = "Obligatorio en el alta", allowableValues = {"bien", "gasto"}, example = "gasto")
    private String tipo;
    @Schema(description = "0 o 1")
    private Byte directo;
    @Schema(description = "0 o 1. El servicio lo guarda pero no filtra por este campo")
    private Byte habilitado;
}
