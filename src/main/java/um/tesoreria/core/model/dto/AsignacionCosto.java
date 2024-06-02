/**
 * 
 */
package um.tesoreria.core.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import um.tesoreria.core.kotlin.model.Articulo;
import um.tesoreria.core.kotlin.model.Comprobante;
import um.tesoreria.core.model.UbicacionArticulo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;
import um.tesoreria.core.kotlin.model.ProveedorMovimiento;

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

	private BigDecimal importe;

}
