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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "contratoperiodo", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "cfp_con_clave", "cfp_anho", "cfp_mes" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ContratoPeriodo extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -7090020857258666836L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cfp_id")
	private Long contratoPeriodoId;

	@Column(name = "cfp_con_clave")
	private Long contratoId;

	@Column(name = "cfp_anho")
	private Integer anho = 0;

	@Column(name = "cfp_mes")
	private Integer mes = 0;

	@Column(name = "cfp_cfa_id")
	private Long contratoFacturaId;

	@Column(name = "cfp_cch_id")
	private Long contratoChequeId;

	@Column(name = "cfp_importe")
	private BigDecimal importe = BigDecimal.ZERO;

	@Column(name = "marca_temporal")
	private Byte marcaTemporal;

	@OneToOne
	@JoinColumn(name = "cfp_con_clave", insertable = false, updatable = false)
	private Contrato contrato;

	public String periodoKey() {
		return this.anho + "." + this.mes;
	}

}
