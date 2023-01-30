/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import ar.edu.um.tesoreria.rest.model.Comprobante;
import ar.edu.um.tesoreria.rest.model.Geografica;
import ar.edu.um.tesoreria.rest.model.Proveedor;
import ar.edu.um.tesoreria.rest.model.ProveedorArticulo;
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
public class ProveedorMovimientoDTO implements Serializable {

	private static final long serialVersionUID = 4720011386734552303L;

	private Long proveedorMovimientoId;
	private Integer proveedorId;
	private String nombreBeneficiario = "";
	private Integer comprobanteId;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaComprobante;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaVencimiento;
	private Integer prefijo = 0;
	private Long numeroComprobante = 0L;
	private BigDecimal netoSinDescuento = BigDecimal.ZERO;
	private BigDecimal descuento = BigDecimal.ZERO;
	private BigDecimal neto = BigDecimal.ZERO;
	private BigDecimal importe = BigDecimal.ZERO;
	private BigDecimal cancelado = BigDecimal.ZERO;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaContable;
	private Integer ordenContable;
	private String concepto;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaAnulacion;
	private Byte conCargo = 0;
	private Byte solicitaFactura = 0;
	private Integer geograficaId;

	private Comprobante comprobante;
	private Proveedor proveedor;
	private Geografica geografica;
	private List<ProveedorArticulo> proveedorArticulos;

}
