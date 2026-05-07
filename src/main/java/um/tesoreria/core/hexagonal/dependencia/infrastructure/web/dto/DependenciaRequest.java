package um.tesoreria.core.hexagonal.dependencia.infrastructure.web.dto;
import lombok.Data;
import java.math.BigDecimal;
@Data
public class DependenciaRequest {
    private Integer dependenciaId;
    private String nombre;
    private String acronimo;
    private Integer facultadId;
    private Integer geograficaId;
    private BigDecimal cuentaHonorariosPagar;
}
