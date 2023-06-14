package um.tesoreria.rest.kotlin.model.view

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.Immutable

@Entity
@Table(name = "vw_tipo_chequera_facultad_lectivo_geografica_cantidad")
@Immutable
data class TipoChequeraFacultadLectivoGeograficaCantidad(

    @Id
    var uniqueId: String? = null,
    var facultadId: Int? = null,
    var lectivoId: Int? = null,
    var geograficaId: Int? = null,
    var tipoChequeraId: Int? = null,
    var nombre: String = "",
    var cantidad: Int = 0

)
