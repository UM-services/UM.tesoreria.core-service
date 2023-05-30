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

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "cpa_fac_id", "cpa_tch_id",
		"cpa_chs_id", "cpa_pro_id", "cpa_alt_id", "cpa_cuo_id", "cpa_orden", "cpa_item" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraPagoAsiento extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1441335232662512579L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "clave")
	private Long chequerapagoasientoId;

	@Column(name = "cpa_fac_id")
	private Integer facultadId;

	@Column(name = "cpa_tch_id")
	private Integer tipochequeraId;

	@Column(name = "cpa_chs_id")
	private Long chequeraserieId;

	@Column(name = "cpa_pro_id")
	private Integer productoId;

	@Column(name = "cpa_alt_id")
	private Integer alternativaId;

	@Column(name = "cpa_cuo_id")
	private Integer cuotaId;

	@Column(name = "cpa_orden")
	private Integer orden;
	
	@Column(name = "cpa_item")
	private Integer item;
	
	@Column(name = "cpa_cuenta")
	private BigDecimal cuenta;

	@Column(name = "cpa_debita")
	private Byte debita = 0;
	
	@Column(name = "cpa_importe")
	private BigDecimal importe = new BigDecimal(0);

}
