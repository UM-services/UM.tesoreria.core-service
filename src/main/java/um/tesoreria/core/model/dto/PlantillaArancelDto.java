/**
 * 
 */
package um.tesoreria.core.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantillaArancelDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4789627026274814987L;
	
	private Integer facultadId;
	private Integer lectivoId;
	private Integer tipochequeraId;
	private Integer aranceltipoId;
	private Integer productoId;
	private Integer alternativaId;
	private Integer cuotaId;
	private Integer mes;
	private Integer anho;
	private BigDecimal porcentaje;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento1;
	private BigDecimal importe1;
	private BigDecimal importe1original;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento2;
	private BigDecimal importe2;
	private BigDecimal importe2original;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento3;
	private BigDecimal importe3;
	private BigDecimal importe3original;
}
