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
import jakarta.persistence.UniqueConstraint;

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
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "facultadId", "tipoChequeraId", "chequeraSerieId" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraEliminada extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7355704417901165054L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chequeraEliminadaId;

	private Integer facultadId;
	private Integer tipoChequeraId;
	private Long chequeraSerieId;
	private BigDecimal personaId;
	private Integer documentoId;
	private Integer lectivoId;
	private Integer arancelTipoId;
	private Integer cursoId;
	private Byte asentado;
	private Integer geograficaId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha;

	private Integer cuotasPagadas;
	private String observaciones;
	private Integer alternativaId;
	private Byte algoPagado;
	private Integer tipoImpresionId;
	private String usuarioId;

}
