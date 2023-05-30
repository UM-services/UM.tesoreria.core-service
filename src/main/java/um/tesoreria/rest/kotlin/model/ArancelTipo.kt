package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.Column

@Entity
@Table
data class ArancelTipo(

	@Id
	@Column(name = "art_id")
	var arancelTipoId: Int? = null,

	@Column(name = "art_descripcion")
	var descripcion: String = "",

	var medioArancel: Byte = 0,
	var arancelTipoIdCompleto: Int? = null

) : Auditable()