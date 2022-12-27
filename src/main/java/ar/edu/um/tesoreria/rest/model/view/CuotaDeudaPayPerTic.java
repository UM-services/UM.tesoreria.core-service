/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_cuota_deuda_ppt")
@Immutable
@NoArgsConstructor
@AllArgsConstructor
public class CuotaDeudaPayPerTic implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -3193314264769714782L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chequeraCuotaId;
	private Integer facultadId;
	private Integer tipoChequeraId;
	private Long chequeraSerieId;
	private Integer productoId;
	private Integer alternativaId;
	private Integer cuotaId;
	private Integer mes;
	private Integer anho;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento1;

	private BigDecimal importe1 = BigDecimal.ZERO;
	private BigDecimal importe1Original = BigDecimal.ZERO;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento2;

	private BigDecimal importe2 = BigDecimal.ZERO;
	private BigDecimal importe2Original;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento3;

	private BigDecimal importe3 = BigDecimal.ZERO;
	private BigDecimal importe3Original = BigDecimal.ZERO;
	private String codigoBarras;
	private String i2Of5;
	private Byte pagado;
	private Byte baja;
	private Byte manual;
	private Integer tramoId;

	public String getKey() {
		String string = String.format("%02d", facultadId);
		string += String.format("%03d", tipoChequeraId);
		string += String.format("%05d", chequeraSerieId);
		string += productoId;
		string += String.format("%02d", alternativaId);
		string += String.format("%03d", cuotaId);
		return string;
	}

}
