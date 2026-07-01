package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArancelPorcentaje {
    private Long arancelporcentajeId;
    private Integer aranceltipoId;
    private Integer productoId;

    @Builder.Default
    private BigDecimal porcentaje = BigDecimal.ZERO;
}
