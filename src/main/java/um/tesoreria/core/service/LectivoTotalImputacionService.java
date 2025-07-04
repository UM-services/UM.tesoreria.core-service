/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.LectivoTotalImputacionException;
import um.tesoreria.core.model.LectivoTotalImputacion;
import um.tesoreria.core.repository.LectivoTotalImputacionRepository;

/**
 * @author daniel
 *
 */
@Service
public class LectivoTotalImputacionService {

	@Autowired
	private LectivoTotalImputacionRepository repository;

	public List<LectivoTotalImputacion> findAllByTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId) {
		return repository.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(facultadId, lectivoId, tipoChequeraId);
	}

	public LectivoTotalImputacion findByProducto(Integer facultadId, Integer lectivoId, Integer tipoChequeraId,
			Integer productoId) {
		return repository
				.findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoId(facultadId, lectivoId, tipoChequeraId,
						productoId)
				.orElseThrow(() -> new LectivoTotalImputacionException(facultadId, lectivoId, tipoChequeraId,
						productoId));
	}

	public LectivoTotalImputacion add(LectivoTotalImputacion lectivoTotalImputacion) {
		lectivoTotalImputacion = repository.save(lectivoTotalImputacion);
		return lectivoTotalImputacion;
	}

	public LectivoTotalImputacion update(LectivoTotalImputacion newLectivoTotalImputacion,
			Long lectivoTotalImputacionId) {
		return repository.findByLectivoTotalImputacionId(lectivoTotalImputacionId).map(lectivoTotalImputacion -> {
			lectivoTotalImputacion = new LectivoTotalImputacion(lectivoTotalImputacionId,
					newLectivoTotalImputacion.getFacultadId(), newLectivoTotalImputacion.getLectivoId(),
					newLectivoTotalImputacion.getTipoChequeraId(), newLectivoTotalImputacion.getProductoId(),
					newLectivoTotalImputacion.getCuenta());
			repository.save(lectivoTotalImputacion);
			return lectivoTotalImputacion;
		}).orElseThrow(() -> new LectivoTotalImputacionException(lectivoTotalImputacionId));
	}

}
