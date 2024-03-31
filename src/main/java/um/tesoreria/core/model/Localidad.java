/**
 * 
 */
package um.tesoreria.core.model;

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
import um.tesoreria.core.kotlin.model.Auditable;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "loc_fac_id", "loc_prv_id", "loc_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Localidad extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8142813137139018953L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "unico_id")
	private Long uniqueId;

	@Column(name = "loc_fac_id")
	private Integer facultadId;

	@Column(name = "loc_prv_id")
	private Integer provinciaId;

	@Column(name = "loc_id")
	private Integer localidadId;

	@Column(name = "loc_nombre")
	private String nombre = "";

}
