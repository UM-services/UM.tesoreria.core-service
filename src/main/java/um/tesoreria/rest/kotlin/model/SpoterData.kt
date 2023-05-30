package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import java.math.BigDecimal

@Entity
@Table
data class SpoterData(

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var spoterDataId: Long? = null,

	var personaId: BigDecimal? = null,
	var documentoId: Int? = null,
	var apellido: String = "",
	var nombre: String = "",
	var facultadId: Int? = null,
	var geograficaId: Int? = null,
	var lectivoId: Int? = null,
	var planId: Int? = null,
	var carreraId: Int? = null,
	var emailPersonal: String = "",
	var celular: String = "",
	var status: Byte = 0,
	var message: String = "",
	var tipoChequeraId: Int? = null,
	var chequeraSerieId: Long? = null,
	var alternativaId: Int? = null


) : Auditable()