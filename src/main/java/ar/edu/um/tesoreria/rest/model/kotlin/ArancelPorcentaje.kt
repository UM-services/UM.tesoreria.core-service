package ar.edu.um.tesoreria.rest.model.kotlin

import ar.edu.um.tesoreria.rest.model.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import java.math.BigDecimal

@Entity
@Table(
	uniqueConstraints = [
		UniqueConstraint(columnNames = ["aranceltipoId", "productoId"])
	]
)
data class ArancelPorcentaje(

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var arancelporcentajeId: Long? = null,

	var aranceltipoId: Int? = null,
	var productoId: Int? = null,
	var porcentaje: BigDecimal = BigDecimal.ZERO

) : Auditable() 