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
		@UniqueConstraint(columnNames = { "csc_fac_id", "csc_tch_id", "csc_chs_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraSerieControl extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8628468519869914909L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "csc_id")
	private Long chequeraSerieControlId;

	@Column(name = "csc_fac_id")
	private Integer facultadId;

	@Column(name = "csc_tch_id")
	private Integer tipoChequeraId;

	@Column(name = "csc_chs_id")
	private Long chequeraSerieId;

	@Column(name = "csc_reemplazada")
	private Byte reemplazada = 0;

	@Column(name = "csc_fac_id_nueva")
	private Integer facultadIdNueva;

	@Column(name = "csc_tch_id_nueva")
	private Integer tipoChequeraIdNueva;

	@Column(name = "csc_chs_id_nueva")
	private Long chequeraSerieIdNueva;

	private Byte eliminada = 0;

}
