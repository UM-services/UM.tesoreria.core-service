/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.dto.CostoParameter;
import ar.edu.um.tesoreria.rest.service.ArticuloService;
import ar.edu.um.tesoreria.rest.service.ComprobanteService;
import ar.edu.um.tesoreria.rest.service.UbicacionArticuloService;
import ar.edu.um.tesoreria.rest.service.UbicacionService;

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

	public CostoParameter findParameters() {
		return new CostoParameter(articuloService.findAll(), comprobanteService.findAll(), ubicacionService.findAll(),
				ubicacionArticuloService.findAll());
	}

}
