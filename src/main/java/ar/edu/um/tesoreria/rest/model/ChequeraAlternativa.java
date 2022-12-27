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
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "cha_fac_id", "cha_tch_id", "cha_chs_id", "cha_pro_id", "cha_alt_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraAlternativa extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2414875787438664687L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cha_id")
	private Long chequeraAlternativaId;

	@Column(name = "cha_fac_id")
	private Integer facultadId;

	@Column(name = "cha_tch_id")
	private Integer tipoChequeraId;

	@Column(name = "cha_chs_id")
	private Long chequeraSerieId;

	@Column(name = "cha_pro_id")
	private Integer productoId;

	@Column(name = "cha_alt_id")
	private Integer alternativaId;

	@Column(name = "cha_titulo")
	private String titulo = "";

	@Column(name = "cha_cuotas")
	private Integer cuotas = 0;

}
