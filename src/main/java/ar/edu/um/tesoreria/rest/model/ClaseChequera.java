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
public class ClaseChequera extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7108406387093539108L;

	@Id
	@Column(name = "cch_id")
	private Integer claseChequeraId;

	@Column(name = "cch_nombre")
	private String nombre;
	
}
