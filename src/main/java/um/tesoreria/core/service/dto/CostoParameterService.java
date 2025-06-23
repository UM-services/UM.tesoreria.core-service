/**
 * 
 */
package um.tesoreria.core.service.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.dto.CostoParameterDto;
import um.tesoreria.core.service.ArticuloService;
import um.tesoreria.core.service.ComprobanteService;
import um.tesoreria.core.service.UbicacionArticuloService;
import um.tesoreria.core.service.UbicacionService;

/**
 * @author daniel
 *
 */
@Service
public class CostoParameterService {

	@Autowired
	private ArticuloService articuloService;

	@Autowired
	private ComprobanteService comprobanteService;

	@Autowired
	private UbicacionService ubicacionService;

	@Autowired
	private UbicacionArticuloService ubicacionArticuloService;

	public CostoParameterDto findParameters() {
		return new CostoParameterDto(articuloService.findAll(), comprobanteService.findAll(), ubicacionService.findAll(),
				ubicacionArticuloService.findAll());
	}

}
