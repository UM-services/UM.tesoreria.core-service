/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.ClaseChequera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.repository.IClaseChequeraRepository;

/**
 * @author daniel
 *
 */
@Service
public class ClaseChequeraService {

	@Autowired
	private IClaseChequeraRepository repository;

	public List<ClaseChequera> findAll() {
		return repository.findAll();
	}

    public List<ClaseChequera> findAllByPosgrado() {
		return repository.findALlByPosgrado((byte) 1);
    }
}
