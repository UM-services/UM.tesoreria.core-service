/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.view;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_contrato_excluido")
@Immutable
@NoArgsConstructor
@AllArgsConstructor
public class ContratoExcluido implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2910454184715384532L;

	@Id
	private String personakey;
	
	private BigDecimal personaId;
	private Integer documentoId;
	private String apellidonombre;
	private BigDecimal total;
	
}
