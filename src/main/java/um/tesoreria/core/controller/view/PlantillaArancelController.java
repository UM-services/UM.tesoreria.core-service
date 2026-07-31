/**
 * 
 */
package um.tesoreria.core.controller.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.dto.PlantillaArancelDto;
import um.tesoreria.core.service.view.PlantillaArancelService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/plantillaarancel")
@RequiredArgsConstructor
public class PlantillaArancelController {
	private final PlantillaArancelService service;

	@GetMapping("/plantilla/{facultadId}/{lectivoId}/{tipochequeraId}/{aranceltipoId}")
	public ResponseEntity<List<PlantillaArancelDto>> findAllByPlantilla(@PathVariable Integer facultadId,
                                                                        @PathVariable Integer lectivoId, @PathVariable Integer tipochequeraId,
                                                                        @PathVariable Integer aranceltipoId) {
		return ResponseEntity.ok(
				service.findAllByPlantilla(facultadId, lectivoId, tipochequeraId, aranceltipoId));
	}
}
