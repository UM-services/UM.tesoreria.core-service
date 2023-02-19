/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.kotlin.ArancelPorcentaje;
import ar.edu.um.tesoreria.rest.model.LectivoCuota;
import ar.edu.um.tesoreria.rest.model.dto.PlantillaArancel;
import ar.edu.um.tesoreria.rest.repository.IArancelPorcentajeRepository;
import ar.edu.um.tesoreria.rest.repository.ILectivoCuotaRepository;

/**
 * @author daniel
 *
 */
@Service
public class PlantillaArancelService {

	@Autowired
	private ILectivoCuotaRepository lectivoCuotaRepository;

	@Autowired
	private IArancelPorcentajeRepository arancelPorcentajeRepository;

	public List<PlantillaArancel> findAllByPlantilla(Integer facultadId, Integer lectivoId, Integer tipoChequeraId,
			Integer arancelTipoId) {
		List<PlantillaArancel> cuotas = new ArrayList<PlantillaArancel>();
		for (LectivoCuota modelo : lectivoCuotaRepository.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(facultadId,
				lectivoId, tipoChequeraId)) {
			ArancelPorcentaje porcentaje = arancelPorcentajeRepository
					.findByAranceltipoIdAndProductoId(arancelTipoId, modelo.getProductoId())
					.orElse(new ArancelPorcentaje());
			BigDecimal importe1 = modelo.getImporte1().multiply(porcentaje.getPorcentaje()).divide(new BigDecimal(100));
			BigDecimal importe2 = modelo.getImporte2().multiply(porcentaje.getPorcentaje()).divide(new BigDecimal(100));
			BigDecimal importe3 = modelo.getImporte3().multiply(porcentaje.getPorcentaje()).divide(new BigDecimal(100));
			cuotas.add(new PlantillaArancel(facultadId, lectivoId, tipoChequeraId, arancelTipoId,
					modelo.getProductoId(), modelo.getAlternativaId(), modelo.getCuotaId(), modelo.getMes(),
					modelo.getAnho(), porcentaje.getPorcentaje(), modelo.getVencimiento1(), importe1,
					modelo.getImporte1(), modelo.getVencimiento2(), importe2, modelo.getImporte2(),
					modelo.getVencimiento3(), importe3, modelo.getImporte3()));
		}
		return cuotas;
	}
}
