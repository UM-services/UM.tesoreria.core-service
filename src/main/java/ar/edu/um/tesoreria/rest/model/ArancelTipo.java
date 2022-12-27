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
public class ArancelTipo extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7609169359202364699L;

	@Id
	@Column(name = "art_id")
	private Integer arancelTipoId;

	@Column(name = "art_descripcion")
	private String descripcion;
	
	private Byte medioArancel;
	private Integer arancelTipoIdCompleto;

}
