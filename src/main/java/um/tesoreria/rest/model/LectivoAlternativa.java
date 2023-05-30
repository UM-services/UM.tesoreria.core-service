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

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "lea_fac_id", "lea_lec_id", "lea_tch_id", "lea_pro_id", "lea_alt_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class LectivoAlternativa extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1039080651447860059L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lea_id")
	private Long lectivoAlternativaId;

	@Column(name = "lea_fac_id")
	private Integer facultadId;

	@Column(name = "lea_lec_id")
	private Integer lectivoId;

	@Column(name = "lea_tch_id")
	private Integer tipoChequeraId;

	@Column(name = "lea_pro_id")
	private Integer productoId;

	@Column(name = "lea_alt_id")
	private Integer alternativaId;

	@Column(name = "lea_titulo")
	private String titulo = "";

	@Column(name = "lea_cuotas")
	private Integer cuotas = 0;

	@Column(name = "lea_descpterm")
	private Integer descuentoPagoTermino = 0;

}
