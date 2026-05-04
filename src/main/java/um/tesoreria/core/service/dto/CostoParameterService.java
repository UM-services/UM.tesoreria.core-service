/**
 * 
 */
package um.tesoreria.core.service.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.dto.CostoParameterDto;
import um.tesoreria.core.hexagonal.articulo.application.service.ArticuloService;
import um.tesoreria.core.service.ComprobanteService;
import um.tesoreria.core.service.UbicacionArticuloService;
import um.tesoreria.core.service.UbicacionService;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class CostoParameterService {

	private final ArticuloService articuloService;
	private final ComprobanteService comprobanteService;
	private final UbicacionService ubicacionService;
	private final UbicacionArticuloService ubicacionArticuloService;

	public CostoParameterDto findParameters() {
		return new CostoParameterDto(articuloService.getAllArticulos(), comprobanteService.findAll(), ubicacionService.findAll(),
				ubicacionArticuloService.findAll());
	}

}
