package um.tesoreria.core.hexagonal.dependencia.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.entity.GeograficaEntity;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.entity.FacultadEntity;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "dependencia")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DependenciaEntity extends Auditable {

    @Id
    private Integer dependenciaId;

    @Builder.Default
    private String nombre = "";

    @Builder.Default
    private String acronimo = "";

    private Integer facultadId;

    private Integer geograficaId;

    private BigDecimal cuentaHonorariosPagar;

    @OneToOne(optional = true)
    @JoinColumn(name = "facultadId", insertable = false, updatable = false)
    private FacultadEntity facultad;

    @OneToOne(optional = true)
    @JoinColumn(name = "geograficaId", insertable = false, updatable = false)
    private GeograficaEntity geografica;

    @OneToOne(optional = true)
    @JoinColumn(name = "cuentaHonorariosPagar", insertable = false, updatable = false)
    private CuentaEntity cuenta;

    public String getSedeKey() {
        return this.facultadId.toString() + "." + this.geograficaId;
    }

}
