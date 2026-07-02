package um.tesoreria.core.kotlin.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity
import um.tesoreria.core.model.Auditable
import java.math.BigDecimal

@Entity
data class Setup(

    @Id
    var setupId: Long? = null,
    var cuotasPermitidas: Int = 0,
    var cuentaHonorariosPagar: BigDecimal? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "cuentaHonorariosPagar", insertable = false, updatable = false)
    var cuenta: CuentaEntity? = null

) : Auditable()
