package um.tesoreria.core.hexagonal.setup.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "setup")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetupEntity extends Auditable {

    @Id
    private Integer setupId;

    private Integer cuotasPermitidas;

    private BigDecimal cuentaHonorariosPagar;

    @OneToOne(optional = true)
    @JoinColumn(name = "cuentaHonorariosPagar", insertable = false, updatable = false)
    private CuentaEntity cuenta;
}
