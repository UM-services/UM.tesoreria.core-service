/**
 * 
 */
package um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.LectivoTotalException;
import um.tesoreria.rest.model.LectivoTotal;
import um.tesoreria.rest.repository.ILectivoTotalRepository;
import um.tesoreria.rest.exception.LectivoTotalException;
import um.tesoreria.rest.repository.ILectivoTotalRepository;

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
