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
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Documento extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -4488497020106093903L;

	@Id
	@Column(name = "doc_id")
	private Integer documentoId;

	@Column(name = "doc_nombre")
	private String nombre = "";

}
