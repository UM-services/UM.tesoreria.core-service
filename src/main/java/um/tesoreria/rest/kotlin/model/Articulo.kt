package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.Column
import java.math.BigDecimal
import jakarta.persistence.OneToOne
import jakarta.persistence.JoinColumn
import org.hibernate.Hibernate

@Entity
@Table(name = "articulos")
data class Articulo(

	@Id
	@Column(name = "art_id")
	var articuloId: Long? = null,

	@Column(name = "art_nombre")
	var nombre: String = "",

	@Column(name = "art_descripcion")
	var descripcion: String = "",

	@Column(name = "art_unidad")
	var unidad: String = "",

	@Column(name = "art_precio")
	var precio: BigDecimal = BigDecimal.ZERO,

	@Column(name = "art_inventariable")
	var inventariable: Byte = 0,

	@Column(name = "art_stockminimo")
	var stockMinimo: Long = 0,

	@Column(name = "art_cuenta")
	var numeroCuenta: BigDecimal? = null,

	@Column(name = "art_tipo")
	var tipo: String = "",

	@Column(name = "art_directo")
	var directo: Byte = 0,

	@Column(name = "art_habilitado")
	var habilitado: Byte = 0,

	@OneToOne(optional = true)
	@JoinColumn(name = "art_cuenta", insertable = false, updatable = false)
	var cuenta: Cuenta? = null

) : Auditable()