package um.tesoreria.core.kotlin.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table
data class Dependencia(

    @Id
    val dependenciaId: Int? = null,
    val nombre: String = "",
    val acronimo: String = "",
    val facultadId: Int? = null,
    val geograficaId: Int? = null,
    val cuentaHonorariosPagar: BigDecimal? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "facultadId", insertable = false, updatable = false)
    val facultad: Facultad? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "geograficaId", insertable = false, updatable = false)
    val geografica: Geografica? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "cuentaHonorariosPagar", insertable = false, updatable = false)
    val cuenta: Cuenta? = null

) : Auditable() {

    constructor(builder: Builder) : this(
        dependenciaId = builder.dependenciaId,
        nombre = builder.nombre,
        acronimo = builder.acronimo,
        facultadId = builder.facultadId,
        geograficaId = builder.geograficaId,
        cuentaHonorariosPagar = builder.cuentaHonorariosPagar,
        facultad = builder.facultad,
        geografica = builder.geografica,
        cuenta = builder.cuenta
    )

    fun getSedeKey(): String {
        return this.facultadId.toString() + "." + this.geograficaId
    }

    class Builder {
        var dependenciaId: Int? = null
        var nombre: String = ""
        var acronimo: String = ""
        var facultadId: Int? = null
        var geograficaId: Int? = null
        var cuentaHonorariosPagar: BigDecimal? = null
        var facultad: Facultad? = null
        var geografica: Geografica? = null
        var cuenta: Cuenta? = null

        fun dependenciaId(dependenciaId: Int?) = apply { this.dependenciaId = dependenciaId }
        fun nombre(nombre: String) = apply { this.nombre = nombre }
        fun acronimo(acronimo: String) = apply { this.acronimo = acronimo }
        fun facultadId(facultadId: Int?) = apply { this.facultadId = facultadId }
        fun geograficaId(geograficaId: Int?) = apply { this.geograficaId = geograficaId }
        fun cuentaHonorariosPagar(cuentaHonorariosPagar: BigDecimal?) = apply { this.cuentaHonorariosPagar = cuentaHonorariosPagar }
        fun facultad(facultad: Facultad?) = apply { this.facultad = facultad }
        fun geografica(geografica: Geografica?) = apply { this.geografica = geografica }
        fun cuenta(cuenta: Cuenta?) = apply { this.cuenta = cuenta }

        fun build() = Dependencia(this)
    }
}