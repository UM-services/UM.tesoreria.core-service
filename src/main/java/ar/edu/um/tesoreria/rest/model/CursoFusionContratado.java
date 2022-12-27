/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;

import jakarta.persistence.Column;
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
public class CursoFusionContratado extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 6505435494868266556L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "curso_fusion_contratado_id")
	private Long cursofusioncontratadoId;

	private Long contratadoId;
	private Integer anho = 0;
	private Integer mes = 0;
	private Integer facultadId;
	private Integer geograficaId;

	@Column(name = "cargo_tipo_id")
	private Integer cargotipoId;

	@Column(name = "designacion_tipo_id")
	private Integer designaciontipoId;

	private Byte anual;
	private Integer categoriaId;

}
