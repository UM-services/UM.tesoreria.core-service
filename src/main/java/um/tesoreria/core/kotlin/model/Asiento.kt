package um.tesoreria.core.kotlin.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.GenerationType
import java.time.OffsetDateTime
import jakarta.persistence.OneToOne
import jakarta.persistence.JoinColumn

@Entity
@Table(
	uniqueConstraints = [
		UniqueConstraint(columnNames = ["fecha", "orden"]),
		UniqueConstraint(columnNames = ["fechaContra", "ordenContra"])
	]
)

data class Asiento(

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	var asientoId: Long? = null,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	var fecha: OffsetDateTime? = null,

	var orden: Int = 0,
	var vinculo: String = "",

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	var fechaContra: OffsetDateTime? = null,

	var ordenContra: Int? = null,
	var trackId: Long? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "trackId", insertable = false, updatable = false)
	var track: Track? = null


) : Auditable()