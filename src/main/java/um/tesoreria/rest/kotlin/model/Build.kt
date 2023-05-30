package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity
@Table
data class Build(

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var build: Long? = null


) : Auditable()