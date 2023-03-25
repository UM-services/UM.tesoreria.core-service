/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;

import ar.edu.um.tesoreria.rest.kotlin.model.Dependencia;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class Ubicacion extends Auditable implements Serializable {

	private static final long serialVersionUID = -7405787922099837849L;

	@Id
	private Integer ubicacionId;
	private String nombre;
	private Integer dependenciaId;

	@OneToOne(optional = true)
	@JoinColumn(name = "dependenciaId", insertable = false, updatable = false)
	private Dependencia dependencia;

}
