package um.tesoreria.rest.kotlin.model

import jakarta.persistence.*

@Entity
@Table
data class Dependencia(

    @Id
    var dependenciaId: Int? = null,
    var nombre: String = "",
    var acronimo: String = "",
    var facultadId: Int? = null,
    var geograficaId: Int? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "facultadId", insertable = false, updatable = false)
    val facultad: Facultad? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "geograficaId", insertable = false, updatable = false)
    var geografica: Geografica? = null,


) : Auditable() {

    fun getSedeKey(): String {
        return this.facultadId.toString() + "." + this.geograficaId
    }

}
