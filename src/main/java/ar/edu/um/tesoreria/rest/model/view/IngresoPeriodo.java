/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.view;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import ar.edu.um.tesoreria.rest.model.view.pk.IngresoPeriodoPk;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_ingreso_periodo")
@IdClass(value = IngresoPeriodoPk.class)
@Immutable
@NoArgsConstructor
@AllArgsConstructor
public class IngresoPeriodo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8168970622963215029L;

	@Id
	private Integer facultadId;

	@Id
	private Integer geograficaId;

	@Id
	private Integer tipopagoId;
	
	@Id
	private Integer mes;

	@Id
	private Integer anho;

	private Integer cantidad;
	private BigDecimal total;

}
