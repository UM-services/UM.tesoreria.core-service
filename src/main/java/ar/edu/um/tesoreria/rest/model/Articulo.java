/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "articulos")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Articulo extends Auditable implements Serializable {

	private static final long serialVersionUID = -7061762659722060404L;

	@Id
	@Column(name = "art_id")
	private Long articuloId;

	@Column(name = "art_nombre")
	private String nombre;

	@Column(name = "art_descripcion")
	private String descripcion;

	@Column(name = "art_unidad")
	private String unidad;

	@Column(name = "art_precio")
	private BigDecimal precio;

	@Column(name = "art_inventariable")
	private Byte inventariable;

	@Column(name = "art_stockminimo")
	private Long stockMinimo;

	@Column(name = "art_cuenta")
	private BigDecimal numeroCuenta;

	@Column(name = "art_tipo")
	private String tipo;

	@Column(name = "art_directo")
	private Byte directo;

	@Column(name = "art_habilitado")
	private Byte habilitado;

	@OneToOne(optional = true)
	@JoinColumn(name = "art_cuenta", insertable = false, updatable = false)
	private Cuenta cuenta;

}
