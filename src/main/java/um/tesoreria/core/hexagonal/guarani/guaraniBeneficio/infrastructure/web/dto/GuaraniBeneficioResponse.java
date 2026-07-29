package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuaraniBeneficioResponse {
    private Integer guaraniBeneficioId;
    private Integer requisito;

    @Builder.Default
    private BigDecimal porcentajeBeneficio = BigDecimal.ZERO;
}
