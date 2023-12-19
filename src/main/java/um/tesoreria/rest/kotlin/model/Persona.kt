package um.tesoreria.rest.kotlin.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.text.MessageFormat

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["per_id", "per_doc_id"])])
data class Persona(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clave")
    var uniqueId: Long? = null,

    @Column(name = "per_id")
    var personaId: BigDecimal? = null,

    @Column(name = "per_doc_id")
    var documentoId: Int? = null,

    @Column(name = "Per_Apellido")
    var apellido: String? = null,

    @Column(name = "Per_Nombre")
    var nombre: String? = null,

    @Column(name = "per_sexo")
    var sexo: String? = null,

    @Column(name = "per_primero")
    var primero: Byte = 0,

    @Column(name = "per_cuit")
    var cuit: String = "",

    @Column(name = "per_cbu")
    var cbu: String = "",

    @Column(name = "per_contrasenha")
    var password: String? = null,

    ) : Auditable() {

    fun getApellidoNombre(): String =
        MessageFormat.format("{0}, {1}", apellido, nombre)

}
