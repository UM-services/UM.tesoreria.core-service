/**
 * 
 */
package um.tesoreria.rest.model;

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
import um.tesoreria.rest.kotlin.model.Auditable;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "mat_fac_id", "mat_pla_id", "mat_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Materia extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -1182475046167967672L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long uniqueId;

	@Column(name = "mat_fac_id")
	private Integer facultadId;

	@Column(name = "mat_pla_id")
	private Integer planId;

	@Column(name = "mat_id")
	private String materiaId;

	@Column(name = "mat_nombre")
	private String nombre = "";

	@Column(name = "mat_opcional")
	private Byte optativa = 0;

	@Column(name = "mat_virtual")
	private Integer virtual = 0;

	@Column(name = "mat_dias")
	private Integer dias = 0;

	@Column(name = "mat_per_id")
	private Integer periodoId = 0;

	@Column(name = "mat_especial")
	private Byte especial = 0;

	@Column(name = "mat_taller")
	private Byte taller = 0;

	@Column(name = "mat_id_real")
	private String materiaIdReal;

	@Column(name = "mat_curso")
	private Integer curso = 0;

	public String getMateriaKey() {
		return this.facultadId + "." + this.planId + "." + this.materiaId;
	}
	
}
