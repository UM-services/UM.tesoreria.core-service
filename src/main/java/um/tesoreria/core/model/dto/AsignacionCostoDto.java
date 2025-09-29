/**
 * 
 */
package um.tesoreria.core.model.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import um.tesoreria.core.kotlin.model.Articulo;
import um.tesoreria.core.kotlin.model.Comprobante;
import um.tesoreria.core.model.UbicacionArticulo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;
import um.tesoreria.core.kotlin.model.ProveedorMovimiento;
import um.tesoreria.core.util.Jsonifier;

/**
 * @author daniel
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionCostoDto implements Serializable {

	@Serial
    private static final long serialVersionUID = -1663996814534996127L;

	private ProveedorMovimiento proveedorMovimiento;
	private ProveedorArticulo proveedorArticulo;
	private Articulo articulo;
	private Comprobante comprobante;
	private UbicacionArticulo ubicacionArticulo;

	private BigDecimal importe;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
