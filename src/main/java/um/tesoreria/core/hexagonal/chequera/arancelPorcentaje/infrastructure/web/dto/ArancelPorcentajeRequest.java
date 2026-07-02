package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ArancelPorcentajeRequest {
    private Integer aranceltipoId;
    private Integer productoId;

    private BigDecimal porcentaje;
}
