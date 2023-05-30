package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table
data class Usuario(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var userId: Long? = null,

    @Column(unique = true)
    var login: String? = null,

    var password: String? = null,
    var nombre: String? = null,
    var geograficaId: Int? = null,
    var imprimeChequera: Byte = 0,
    var numeroOpManual: Byte = 0,
    var habilitaOpEliminacion: Byte = 0,
    var eliminaChequera: Byte = 0,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var lastLog: OffsetDateTime? = null

) : Auditable()
