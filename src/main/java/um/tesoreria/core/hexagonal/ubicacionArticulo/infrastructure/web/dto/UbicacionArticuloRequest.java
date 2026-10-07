package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UbicacionArticuloRequest {
    @Schema(description = "Obligatorio; tiene que existir", example = "1")
    private Integer ubicacionId;
    @Schema(description = "Obligatorio; tiene que existir", example = "465")
    private Long articuloId;
    @Schema(description = "Cuenta del vínculo; nula lo deja sin cuenta. Si viene, tiene que existir en el plan de cuentas")
    private BigDecimal numeroCuenta;
}
