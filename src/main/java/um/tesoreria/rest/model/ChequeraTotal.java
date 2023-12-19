/**
 * 
 */
package um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;

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
		@UniqueConstraint(columnNames = { "cht_fac_id", "cht_tch_id", "cht_chs_id", "cht_pro_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraTotal extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -8950753856481968525L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cht_id")
	private Long chequeraTotalId;

	@Column(name = "cht_fac_id")
	private Integer facultadId;

	@Column(name = "cht_tch_id")
	private Integer tipoChequeraId;

	@Column(name = "cht_chs_id")
	private Long chequeraSerieId;

	@Column(name = "cht_pro_id")
	private Integer productoId;

	@Column(name = "cht_total")
	private BigDecimal total = BigDecimal.ZERO;

	@Column(name = "cht_pagado")
	private BigDecimal pagado = BigDecimal.ZERO;

}
