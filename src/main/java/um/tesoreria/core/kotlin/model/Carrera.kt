package um.tesoreria.core.kotlin.model

import jakarta.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["car_fac_id", "car_pla_id", "car_id"])])
data class Carrera(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clave")
    var uniqueId: Long? = null,

    @Column(name = "car_fac_id")
    var facultadId: Int? = null,

    @Column(name = "car_pla_id")
    var planId: Int? = null,

    @Column(name = "car_id")
    var carreraId: Int? = null,

    @Column(name = "car_nombre")
    var nombre: String = "",

    @Column(name = "car_iniciales")
    var iniciales: String = "",

    @Column(name = "car_titulo")
    var titulo: String = "",

    @Column(name = "trabajo_final")
    var trabajofinal: Byte = 0,

    var resolucion: String = "",

    @Column(name = "car_chequnica")
    var chequeraunica: Byte = 0,

    var bloqueId: Int? = null,
    var obligatorias: Int = 0,
    var optativas: Int = 0,
    var vigente: Byte = 0,

    @OneToOne
    @JoinColumns(
        JoinColumn(name = "car_pla_id", referencedColumnName = "pla_id", insertable = false, updatable = false),
        JoinColumn(name = "car_fac_id", referencedColumnName = "pla_fac_id", insertable = false, updatable = false)
    )
    var plan: Plan? = null,

) : Auditable() {

    fun getCarreraKey(): String {
        return this.facultadId.toString() + "." + this.planId + "." + this.carreraId
    }
}
