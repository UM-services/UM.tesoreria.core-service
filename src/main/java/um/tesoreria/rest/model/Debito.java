/**
 * 
 */
package um.tesoreria.rest.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import um.tesoreria.rest.kotlin.model.Auditable;
import um.tesoreria.rest.kotlin.model.ChequeraSerie;
import um.tesoreria.rest.kotlin.model.Producto;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "facultadId", "tipoChequeraId", "chequeraSerieId",
		"productoId", "alternativaId", "cuotaId", "debitoTipoId" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Debito extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 7517468625898950334L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long debitoId;

	private Integer facultadId;
	private Integer tipoChequeraId;
	private Long chequeraSerieId;
	private Long chequeraId;
	private Integer productoId;
	private Integer alternativaId;
	private Integer cuotaId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaVencimiento;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaBaja;

	private Integer debitoTipoId;
	private String cbu;
	private String numeroTarjeta;
	private String observaciones = "";

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaEnvio;

	private Byte rechazado = 0;
	private String motivoRechazo = "";
	private Long archivoBancoId;

	@OneToOne(optional = true)
	@JoinColumn(name = "chequeraId", referencedColumnName = "clave", insertable = false, updatable = false)
	private ChequeraSerie chequeraSerie;

	@OneToOne(optional = true)
	@JoinColumn(name = "productoId", referencedColumnName = "pro_id", insertable = false, updatable = false)
	private Producto producto;

	public String chequeraKey() {
		return this.facultadId + "." + this.tipoChequeraId + "." + this.chequeraSerieId;
	}

	public String cuotaKey() {
		return this.chequeraKey() + "." + this.productoId + "." + this.alternativaId + "." + this.cuotaId;
	}

}
