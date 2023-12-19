/**
 * 
 */
package um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import um.tesoreria.rest.kotlin.model.Articulo;
import um.tesoreria.rest.kotlin.model.Auditable;
import um.tesoreria.rest.kotlin.model.Cuenta;
import um.tesoreria.rest.kotlin.model.Ubicacion;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "ubicacionId", "articuloId" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionArticulo extends Auditable implements Serializable {

	private static final long serialVersionUID = -8633226884908426808L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ubicacionArticuloId;

	private Integer ubicacionId;
	private Long articuloId;
	
	@Column(name = "cuenta_contable")
	private BigDecimal numeroCuenta;

	@OneToOne(optional = true)
	@JoinColumn(name = "ubicacionId", insertable = false, updatable = false)
	private Ubicacion ubicacion;

	@OneToOne(optional = true)
	@JoinColumn(name = "articuloId", insertable = false, updatable = false)
	private Articulo articulo;

	@OneToOne(optional = true)
	@JoinColumn(name = "cuenta_contable", insertable = false, updatable = false)
	private Cuenta cuenta;

}
