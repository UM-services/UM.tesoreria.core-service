package um.tesoreria.core.kotlin.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import java.time.OffsetDateTime
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.OneToOne
import jakarta.persistence.JoinColumn

@Entity
data class Entrega (
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "noe_id")
	var entregaId: Long? = null,

	@Column(name = "noe_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	var fecha: OffsetDateTime? = null,

	@Column(name = "noe_ubi_id")
	var ubicacionId: Int? = null,

	@Column(name = "noe_recibe")
	var recibe: String = "",

	@Column(name = "noe_observacion")
	var observacion: String = "",

	@Column(name = "noe_mco_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	var fechaContable: OffsetDateTime? = null,

	@Column(name = "noe_mco_nrocomp")
	var ordenContable: Int = 0,

	@Column(name = "noe_anulada")
	var anulada: Byte = 0,

	@Column(name = "noe_tipo")
	var tipo: String = "",

	var trackId: Long? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "noe_ubi_id", insertable = false, updatable = false)
	var ubicacion: Ubicacion? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "trackId", insertable = false, updatable = false)
	var track: Track? = null

) : Auditable() {

	companion object {
		fun builder() = Builder()
	}

	class Builder {
		private var entregaId: Long? = null
		private var fecha: OffsetDateTime? = null
		private var ubicacionId: Int? = null
		private var recibe: String = ""
		private var observacion: String = ""
		private var fechaContable: OffsetDateTime? = null
		private var ordenContable: Int = 0
		private var anulada: Byte = 0
		private var tipo: String = ""
		private var trackId: Long? = null
		private var ubicacion: Ubicacion? = null
		private var track: Track? = null

		fun entregaId(entregaId: Long?) = apply { this.entregaId = entregaId }
		fun fecha(fecha: OffsetDateTime?) = apply { this.fecha = fecha }
		fun ubicacionId(ubicacionId: Int?) = apply { this.ubicacionId = ubicacionId }
		fun recibe(recibe: String) = apply { this.recibe = recibe }
		fun observacion(observacion: String) = apply { this.observacion = observacion }
		fun fechaContable(fechaContable: OffsetDateTime?) = apply { this.fechaContable = fechaContable }
		fun ordenContable(ordenContable: Int) = apply { this.ordenContable = ordenContable }
		fun anulada(anulada: Byte) = apply { this.anulada = anulada }
		fun tipo(tipo: String) = apply { this.tipo = tipo }
		fun trackId(trackId: Long?) = apply { this.trackId = trackId }
		fun ubicacion(ubicacion: Ubicacion?) = apply { this.ubicacion = ubicacion }
		fun track(track: Track?) = apply { this.track = track }

		fun build() = Entrega(
			entregaId,
			fecha,
			ubicacionId,
			recibe,
			observacion,
			fechaContable,
			ordenContable,
			anulada,
			tipo,
			trackId,
			ubicacion,
			track
		)
	}
}