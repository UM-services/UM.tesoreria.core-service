/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import ar.edu.um.tesoreria.rest.util.Tool;
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
@Table(name = "ejercicios")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Ejercicio extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2680545663863027580L;

	@Id
	@Column(name = "eje_id")
	private Integer ejercicioId;

	@Column(name = "eje_nombre")
	private String nombre;

	@Column(name = "eje_fechainicio")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaInicio = Tool.dateAbsoluteArgentina();

	@Column(name = "eje_fechafin")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaFinal = Tool.dateAbsoluteArgentina();

	@Column(name = "eje_bloqueado")
	private Byte bloqueado;

	private Integer ordenContableResultado;
	private Integer ordenContableBienesUso;

}
