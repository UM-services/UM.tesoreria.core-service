package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArancelPorcentajeResponse {
    private Long arancelporcentajeId;
    private Integer aranceltipoId;
    private Integer productoId;
    private BigDecimal porcentaje;
}
