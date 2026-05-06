package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UbicacionArticuloRequest {
    private Integer ubicacionId;
    private Long articuloId;
    private BigDecimal numeroCuenta;
}
