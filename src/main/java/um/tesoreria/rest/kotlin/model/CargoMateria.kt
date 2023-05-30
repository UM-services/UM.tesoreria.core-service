package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.Column


@Entity
@Table(name = "cargomateria")
data class CargoMateria(

	@Id
	@Column(name = "cam_id")
	var cargomateriaId: Int? = null,

	@Column(name = "cam_nombre")
	var nombre: String = "",

	@Column(name = "cam_precedencia")
	var precedencia: Int = 0

) : Auditable()