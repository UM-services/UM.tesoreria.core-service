package um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "facultad")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultadEntity extends Auditable {

    @Id
    @Column(name = "fac_id")
    private Integer facultadId;

    @Builder.Default
    @Column(name = "fac_nombre")
    private String nombre = "";

    @Builder.Default
    @Column(name = "fac_codigo_empresa")
    private String codigoempresa = "";

    @Builder.Default
    @Column(name = "fac_server")
    private String server = "";

    @Builder.Default
    @Column(name = "fac_db_adm")
    private String dbadm = "";

    @Builder.Default
    @Column(name = "fac_dsn")
    private String dsn = "";

    @Builder.Default
    @Column(name = "fac_pla_cuenta")
    private BigDecimal cuentacontable = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "api_server")
    private String apiserver = "";

    private Integer guaraniResponsableAcademica;

    @Builder.Default
    @Column(name = "api_port")
    private Long apiport = 0L;

}
