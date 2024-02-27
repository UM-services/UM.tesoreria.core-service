package um.tesoreria.rest.kotlin.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.Column
import java.math.BigDecimal
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.OffsetDateTime
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.OneToOne
import jakarta.persistence.JoinColumn

@Entity
@Table(name = "plancta")
data class Cuenta(

    @Id
    @Column(name = "pla_cuenta")
    var numeroCuenta: BigDecimal? = null,

    @Column(name = "pla_nombre")
    var nombre: String = "",

    @Column(name = "pla_integra")
    var integradora: Byte = 0,

    @Column(name = "pla_grado")
    var grado: Int = 0,

    @Column(name = "pla_grado1")
    var grado1: BigDecimal? = null,

    @Column(name = "pla_grado2")
    var grado2: BigDecimal? = null,

    @Column(name = "pla_grado3")
    var grado3: BigDecimal? = null,

    @Column(name = "pla_grado4")
    var grado4: BigDecimal? = null,

    var geograficaId: Int? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaBloqueo: OffsetDateTime? = null,

    var visible: Byte = 0,

    @Column(name = "id")
    var cuentaContableId: Long? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "geograficaId", insertable = false, updatable = false)
    var geografica: Geografica? = null

) : Auditable()