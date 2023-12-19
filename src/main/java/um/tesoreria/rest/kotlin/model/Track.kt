package um.tesoreria.rest.kotlin.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity
@Table
data class Track(

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var trackId: Long? = null,

	var descripcion: String = ""


) : Auditable()
