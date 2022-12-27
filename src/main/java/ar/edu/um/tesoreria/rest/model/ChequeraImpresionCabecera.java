/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table(name = "chequera_c_impresion")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraImpresionCabecera extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 7635128584554429838L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chequeraImpresionCabeceraId;

	private Integer facultadId;
	private Integer tipochequeraId;
	private Long chequeraserieId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha;

	private BigDecimal personaId;
	private Integer documentoId;
	private Integer lectivoId;
	private Integer geograficaId;
	private Integer aranceltipoId;
	private Integer alternativaId;
	private String usuarioId;	

}
