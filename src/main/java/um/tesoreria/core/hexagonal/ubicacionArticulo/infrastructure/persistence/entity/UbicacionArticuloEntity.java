/**
 * 
 */
package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity;

import java.io.Serial;
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

import lombok.*;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.entity.ArticuloEntity;
import um.tesoreria.core.kotlin.model.Auditable;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.ubicacion.infrastructure.persistence.entity.UbicacionEntity;

/**
 * @author daniel
 *
 */
@Getter
@Setter
@Entity
@Table(name = "ubicacion_articulo", uniqueConstraints = { @UniqueConstraint(columnNames = { "ubicacionId", "articuloId" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionArticuloEntity extends Auditable implements Serializable {

	@Serial
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
	private UbicacionEntity ubicacion;

	@OneToOne(optional = true)
	@JoinColumn(name = "articuloId", insertable = false, updatable = false)
	private ArticuloEntity articulo;

	@OneToOne(optional = true)
	@JoinColumn(name = "cuenta_contable", insertable = false, updatable = false)
	private CuentaEntity cuenta;

}
