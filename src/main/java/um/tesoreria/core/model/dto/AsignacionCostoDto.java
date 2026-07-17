/**
 * 
 */
package um.tesoreria.core.model.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.entity.ArticuloEntity;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.entity.ComprobanteEntity;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity.UbicacionArticuloEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.ProveedorMovimientoEntity;
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

	private ProveedorMovimientoEntity proveedorMovimiento;
	private ProveedorArticulo proveedorArticulo;
	private ArticuloEntity articulo;
	private ComprobanteEntity comprobante;
	private UbicacionArticuloEntity ubicacionArticulo;

	private BigDecimal importe;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
