package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GuaraniBeneficioRequest {
    private Integer requisito;
    private BigDecimal porcentajeBeneficio;
}
