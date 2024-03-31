/**
 * 
 */
package um.tesoreria.core.model.dto;

import java.io.Serializable;
import java.util.List;

import um.tesoreria.core.kotlin.model.Comprobante;
import um.tesoreria.core.kotlin.model.Ubicacion;
import um.tesoreria.core.kotlin.model.Articulo;
import um.tesoreria.core.model.UbicacionArticulo;
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
public class CostoParameter implements Serializable {

	private static final long serialVersionUID = 3389089202450536124L;

	private List<Articulo> articulos;
	private List<Comprobante> comprobantes;
	private List<Ubicacion> ubicacions;
	private List<UbicacionArticulo> ubicacionArticulos;

}
