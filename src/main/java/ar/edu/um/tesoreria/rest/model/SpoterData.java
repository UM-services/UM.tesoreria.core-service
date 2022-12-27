/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class SpoterData extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 211490393735165122L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long spoterDataId;

	private BigDecimal personaId;
	private Integer documentoId;
	private String apellido = "";
	private String nombre = "";
	private Integer facultadId;
	private Integer geograficaId;
	private Integer lectivoId;
	private Integer planId;
	private Integer carreraId;
	private String emailPersonal = "";
	private String celular = "";
	private Byte status = 0;
	private String message = "";
	private Integer tipoChequeraId;
	private Long chequeraSerieId;
	private Integer alternativaId;

}
