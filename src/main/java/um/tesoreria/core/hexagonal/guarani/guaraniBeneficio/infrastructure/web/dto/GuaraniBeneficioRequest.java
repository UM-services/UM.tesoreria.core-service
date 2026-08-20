package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.dto;

import lombok.Data;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class GuaraniBeneficioRequest {
    @NotNull
    private Integer requisito;

    @NotNull
    @DecimalMin(value = "0", inclusive = true)
    @DecimalMax(value = "100", inclusive = true)
    private BigDecimal porcentajeBeneficio;
}
