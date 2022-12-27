/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.view;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import ar.edu.um.tesoreria.rest.model.Auditable;
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
@Table(name = "vw_producto_tipochequera")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProductoTipoChequera extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6310318043044921867L;

	@Id
	private Long lectivototalId;

	@Column(name = "pro_id")
	private Integer productoId;
	
	@Column(name = "pro_nombre")
	private String nombre;

	private Integer facultadId;
	private Integer lectivoId;
	private Integer tipochequeraId;

}
