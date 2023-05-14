/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import ar.edu.um.tesoreria.rest.kotlin.model.Articulo;
import ar.edu.um.tesoreria.rest.kotlin.model.Comprobante;
import ar.edu.um.tesoreria.rest.kotlin.model.ProveedorArticulo;
import ar.edu.um.tesoreria.rest.kotlin.model.ProveedorMovimiento;
import com.fasterxml.jackson.annotation.JsonFormat;

import ar.edu.um.tesoreria.rest.model.UbicacionArticulo;
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
public class AsignacionCosto implements Serializable {

	private static final long serialVersionUID = -1663996814534996127L;

	private ProveedorMovimiento proveedorMovimiento;
	private ProveedorArticulo proveedorArticulo;
	private Articulo articulo;
	private Comprobante comprobante;
	private UbicacionArticulo ubicacionArticulo;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha;

	private BigDecimal importe;

}
