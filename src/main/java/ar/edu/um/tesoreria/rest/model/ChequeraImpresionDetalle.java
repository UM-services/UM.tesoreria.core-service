/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table(name = "chequera_d_impresion")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraImpresionDetalle extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 7700613846583473862L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chequeraImpresionDetalleId;

	private Long chequeraImpresionCabeceraId;
	private Integer facultadId;
	private Integer tipochequeraId;
	private Long chequeraserieId;
	private Integer productoId;
	private Integer alternativaId;
	private Integer cuotaId;
	private String titulo = "";
	private BigDecimal total = BigDecimal.ZERO;
	private Integer cuotas = 0;
	private Integer mes = 0;
	private Integer anho = 0;
	private Integer arancelTipoId;

	@Column(name = "vencimiento_1")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento1;

	@Column(name = "importe_1")
	private BigDecimal importe1 = BigDecimal.ZERO;

	@Column(name = "vencimiento_2")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento2;

	@Column(name = "importe_2")
	private BigDecimal importe2 = BigDecimal.ZERO;

	@Column(name = "vencimiento_3")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento3;

	@Column(name = "importe_3")
	private BigDecimal importe3 = BigDecimal.ZERO;

	private String usuarioId;

}
