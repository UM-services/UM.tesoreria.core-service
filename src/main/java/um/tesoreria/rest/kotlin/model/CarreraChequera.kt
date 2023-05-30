package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
	uniqueConstraints = [
		UniqueConstraint(
			columnNames = [
				"lectivoId", "facultadId", "planId", "carreraId",
				"claseChequeraId", "curso", "geograficaId"
			]
		)
	]
)
data class CarreraChequera(

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	var carreraChequeraId: Long? = null,

	var facultadId: Int? = null,
	var lectivoId: Int? = null,
	var planId: Int? = null,
	var carreraId: Int? = null,
	var claseChequeraId: Int? = null,
	var curso: Int = 0,
	var geograficaId: Int? = null,
	var tipoChequeraId: Int? = null,
) : Auditable() {

	fun getCarreraKey(): String {
		return "$facultadId.$planId.$carreraId"
	}

}
