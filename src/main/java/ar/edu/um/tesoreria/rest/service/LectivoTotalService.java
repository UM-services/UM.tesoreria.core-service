/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.LectivoTotalException;
import ar.edu.um.tesoreria.rest.model.LectivoTotal;
import ar.edu.um.tesoreria.rest.repository.ILectivoTotalRepository;

/**
 * @author daniel
 *
 */
@Service
public class LectivoTotalService {

	@Autowired
	private ILectivoTotalRepository repository;

	public List<LectivoTotal> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

	public List<LectivoTotal> findAllByLectivo(Integer facultadId, Integer lectivoId) {
		return repository.findAllByFacultadIdAndLectivoId(facultadId, lectivoId);
	}

	public List<LectivoTotal> findAllByTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId) {
		return repository.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(facultadId, lectivoId, tipoChequeraId);
	}

	public LectivoTotal findByProducto(Integer facultadId, Integer lectivoId, Integer tipoChequeraId,
			Integer productoId) {
		return repository
				.findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoId(facultadId, lectivoId, tipoChequeraId,
						productoId)
				.orElseThrow(
						() -> new LectivoTotalException(facultadId, lectivoId, tipoChequeraId, productoId));
	}

}
