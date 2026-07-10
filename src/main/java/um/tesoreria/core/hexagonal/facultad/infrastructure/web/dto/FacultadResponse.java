package um.tesoreria.core.hexagonal.facultad.infrastructure.web.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultadResponse {
    private Integer facultadId;
    private String nombre;
    private String codigoempresa;
    private String server;
    private String dbadm;
    private String dsn;
    private BigDecimal cuentacontable;
    private String apiserver;
    private Long apiport;
    private Integer guaraniResponsableAcademica;
}
