package um.tesoreria.core.hexagonal.dependencia.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.entity.FacultadEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dependencia {
    private Integer dependenciaId;
    private String nombre;
    private String acronimo;
    private Integer facultadId;
    private Integer geograficaId;
    private BigDecimal cuentaHonorariosPagar;
    private OffsetDateTime fechaAuditoria;
    private String usuarioAuditoria;
    private FacultadEntity facultad;
    private Geografica geografica;
    private Cuenta cuenta;

    public String getSedeKey() {
        return this.facultadId.toString() + "." + this.geograficaId;
    }
}
