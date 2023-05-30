/**
 * 
 */
package um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.kotlin.model.ClaseChequera;
import um.tesoreria.rest.repository.IClaseChequeraRepository;
import um.tesoreria.rest.repository.IClaseChequeraRepository;

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
