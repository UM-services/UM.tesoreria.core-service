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
import jakarta.persistence.UniqueConstraint;

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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "prv_fac_id", "prv_id" }),
		@UniqueConstraint(columnNames = { "prv_fac_id", "prv_nombre" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Provincia extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 832107545199524148L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "unico_id")
	private Long uniqueId;

	@Column(name = "prv_fac_id")
	private Integer facultadId;

	@Column(name = "prv_id")
	private Integer provinciaId;

	@Column(name = "prv_nombre")
	private String nombre = "";

	@Column(name = "prv_codsantander")
	private Integer codigosantander;

}
