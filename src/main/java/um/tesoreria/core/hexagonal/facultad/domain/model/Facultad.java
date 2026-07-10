package um.tesoreria.core.hexagonal.facultad.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facultad {
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
    private OffsetDateTime fechaAuditoria;
    private String usuarioAuditoria;
}
