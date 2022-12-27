/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class Curso extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6015043428683272219L;
	
	@Id
	@Column(name = "cur_id")
	private Integer cursoId;
	
	@Column(name = "cur_nombre")
	private String nombre;
	
	private Integer claseChequeraId;

}
