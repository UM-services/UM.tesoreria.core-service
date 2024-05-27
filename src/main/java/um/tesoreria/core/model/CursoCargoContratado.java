/**
 * 
 */
package um.tesoreria.core.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import um.tesoreria.core.kotlin.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import um.tesoreria.core.kotlin.model.view.ContratadoPersona;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "cursoId", "anho", "mes", "contratadoId" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class CursoCargoContratado extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 3440583907430980457L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cursoCargoContratadoId;

	private Long cursoId;
	private Integer anho = 0;
	private Integer mes = 0;
	private Long contratadoId;
	private Long contratoId;
	private Integer cargoTipoId;
	private BigDecimal horasSemanales = BigDecimal.ZERO;
	private BigDecimal horasTotales = BigDecimal.ZERO;
	private Integer designacionTipoId;
	private Integer categoriaId;
	private Long cursoCargoNovedadId;
	private Byte acreditado = 0;

	@OneToOne
	@JoinColumn(name = "contratadoId", insertable = false, updatable = false)
	private ContratadoPersona contratadoPersona;

	public String getPeriodo() {
		return this.anho + "." + this.mes;
	}

}
