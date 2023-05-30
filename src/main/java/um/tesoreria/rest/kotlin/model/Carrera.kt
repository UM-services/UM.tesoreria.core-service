package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

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

    ) : Auditable() {

    fun getCarreraKey(): String {
        return this.facultadId.toString() + "." + this.planId + "." + this.carreraId
    }

}
