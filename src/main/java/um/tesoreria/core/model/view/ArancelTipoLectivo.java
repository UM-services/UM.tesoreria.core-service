/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

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
@Table(name = "vw_aranceltipo_lectivo")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ArancelTipoLectivo extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1449001314127459147L;

	@Id
	@Column(name = "unified")
	private String unified;

	@Column(name = "art_id")
	private Integer arancelTipoId;

	@Column(name = "art_descripcion")
	private String descripcion;

	private Integer lectivoId;
	
}
