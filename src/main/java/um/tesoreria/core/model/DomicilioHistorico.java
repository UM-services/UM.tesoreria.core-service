/**
 * 
 */
package um.tesoreria.core.model;

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
import um.tesoreria.core.kotlin.model.Auditable;

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
public class DomicilioHistorico extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1211524960568117961L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long domicilioHistoricoId;

	private BigDecimal personaId;
	private Integer documentoId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha;

	private String calle = "";
	private String puerta = "";
	private String piso = "";
	private String dpto = "";
	private String telefono = "";
	private String movil = "";
	private String observaciones = "";
	private String codigoPostal = "";
	private String provincia = "";
	private String localidad = "";
	private String mailPersonal = "";
	private String mailInstitucional = "";
	private String domicilioLaboral;

}
