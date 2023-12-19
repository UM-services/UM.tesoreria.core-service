/**
 * 
 */
package um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table(name = "paypertic")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PayPerTic extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -5608890689049170003L;

	@Id
	@Column(name = "paypertic_id")
	private String payperticId;

	@Column(name = "external_transaction_id")
	private String externaltransactionId;

	private String conceptId;

	@Column(name = "concept_description")
	private String conceptdescription;

	@Column(name = "payer_name")
	private String payername;

	@Column(name = "payer_email")
	private String payeremail;

	@Column(name = "payer_number_id")
	private String payernumberId;

	@Column(name = "upload_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime uploaddate;

	@Column(name = "due_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime duedate;

	@Column(name = "payment_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime paymentdate;

	@Column(name = "accreditation_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime accreditationdate;

	private BigDecimal amount = new BigDecimal(0);

	@Column(name = "arancel_recaudacion")
	private BigDecimal arancelrecaudacion = new BigDecimal(0);

	@Column(name = "iva_servicio")
	private BigDecimal ivaservicio = new BigDecimal(0);

	@Column(name = "costo_medio_pago")
	private BigDecimal costomediopago = new BigDecimal(0);

	@Column(name = "costo_financiacion")
	private BigDecimal costofinanciacion = new BigDecimal(0);

	@Column(name = "otros_costos")
	private BigDecimal otroscostos = new BigDecimal(0);

	@Column(name = "impuestos_refacturacion")
	private BigDecimal impuestosrefacturacion = new BigDecimal(0);

	@Column(name = "total_factura")
	private BigDecimal totalfactura = new BigDecimal(0);

	private String sheet;
	private Byte importado = 0;

}
