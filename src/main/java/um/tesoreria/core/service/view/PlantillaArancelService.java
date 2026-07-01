/**
 * 
 */
package um.tesoreria.core.service.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.persistence.entity.ArancelPorcentajeEntity;
import um.tesoreria.core.model.LectivoCuota;
import um.tesoreria.core.model.dto.PlantillaArancelDto;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.persistence.repository.JpaArancelPorcentajeRepository;
import um.tesoreria.core.repository.LectivoCuotaRepository;

/**
 * @author daniel
 *
 */
@Service
public class PlantillaArancelService {

	@Autowired
	private LectivoCuotaRepository lectivoCuotaRepository;

	@Autowired
	private JpaArancelPorcentajeRepository arancelPorcentajeRepository;

	public List<PlantillaArancelDto> findAllByPlantilla(Integer facultadId, Integer lectivoId, Integer tipoChequeraId,
                                                        Integer arancelTipoId) {
		List<PlantillaArancelDto> cuotas = new ArrayList<PlantillaArancelDto>();
		for (LectivoCuota modelo : lectivoCuotaRepository.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(facultadId,
				lectivoId, tipoChequeraId)) {
			ArancelPorcentajeEntity porcentaje = arancelPorcentajeRepository
					.findByAranceltipoIdAndProductoId(arancelTipoId, modelo.getProductoId())
					.orElse(new ArancelPorcentajeEntity());
			BigDecimal importe1 = modelo.getImporte1().multiply(porcentaje.getPorcentaje()).divide(new BigDecimal(100));
			BigDecimal importe2 = modelo.getImporte2().multiply(porcentaje.getPorcentaje()).divide(new BigDecimal(100));
			BigDecimal importe3 = modelo.getImporte3().multiply(porcentaje.getPorcentaje()).divide(new BigDecimal(100));
			cuotas.add(new PlantillaArancelDto(facultadId, lectivoId, tipoChequeraId, arancelTipoId,
					modelo.getProductoId(), modelo.getAlternativaId(), modelo.getCuotaId(), modelo.getMes(),
					modelo.getAnho(), porcentaje.getPorcentaje(), modelo.getVencimiento1(), importe1,
					modelo.getImporte1(), modelo.getVencimiento2(), importe2, modelo.getImporte2(),
					modelo.getVencimiento3(), importe3, modelo.getImporte3()));
		}
		return cuotas;
	}
}
