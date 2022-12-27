/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "facultadId", "personaId",
		"documentoId", "lectivoId", "clasechequeraId" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Matricula extends Auditable implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8178972804972758636L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long matriculaId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha;

	private Integer facultadId;
	private BigDecimal personaId;
	private Integer documentoId;
	private Integer lectivoId;
	private Integer geograficaId;
	private Integer planId;
	private Integer carreraId;
	private Integer tipochequeraId;
	private Long chequeraserieId;
	private Integer curso = 0;

	@Column(name = "cantidad_materias_matricula")
	private Integer cantidadmateriasmatricula = 0;

	@Column(name = "cantidad_materias_curso")
	private Integer cantidadmateriascurso = 0;

	@Column(name = "chequera_pendiente")
	private Byte chequerapendiente = 1;

	private Byte provisoria = 0;
	private Integer clasechequeraId;

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
