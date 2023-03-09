package ar.edu.um.tesoreria.rest.model.kotlin

import ar.edu.um.tesoreria.rest.model.Auditable
import ar.edu.um.tesoreria.rest.model.Ubicacion
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
@Table
data class Entrega(

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

) : Auditable()