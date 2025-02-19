package um.tesoreria.core.kotlin.model

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