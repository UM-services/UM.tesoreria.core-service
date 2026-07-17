/**
 * 
 */
package um.tesoreria.core.model.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.ubicacion.domain.model.Ubicacion;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
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
public class CostoParameterDto implements Serializable {

	@Serial
    private static final long serialVersionUID = 3389089202450536124L;

	private List<Articulo> articulos;
	private List<Comprobante> comprobantes;
	private List<Ubicacion> ubicacions;
	private List<UbicacionArticulo> ubicacionArticulos;

}
