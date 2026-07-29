package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuaraniBeneficio {
    private Integer guaraniBeneficioId;
    private Integer requisito;

    @Builder.Default
    private BigDecimal porcentajeBeneficio = BigDecimal.ZERO;
}
