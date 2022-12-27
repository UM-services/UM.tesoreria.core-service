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
public class Setup extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4771386991119185930L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long setupId;

	@Column(name = "cuotas_permitidas")
	private Integer cuotaspermitidas = 0;

}
