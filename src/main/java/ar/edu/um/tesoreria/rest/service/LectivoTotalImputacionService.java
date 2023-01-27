/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.LectivoTotalImputacionException;
import ar.edu.um.tesoreria.rest.model.LectivoTotalImputacion;
import ar.edu.um.tesoreria.rest.repository.ILectivoTotalImputacionRepository;

/**
 * @author daniel
 *
 */
@Service
public class LectivoTotalImputacionService {

	@Autowired
	private ILectivoTotalImputacionRepository repository;

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
