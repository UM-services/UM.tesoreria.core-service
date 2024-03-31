/**
 *
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.ClaseChequera;
import um.tesoreria.core.repository.IClaseChequeraRepository;

/**
 * @author daniel
 */
@Service
public class ClaseChequeraService {

    @Autowired
    private IClaseChequeraRepository repository;

    public List<ClaseChequera> findAll() {
        return repository.findAll();
    }

    public List<ClaseChequera> findAllByPosgrado() {
        return repository.findAllByPosgrado((byte) 1);
    }

    public List<ClaseChequera> findAllByCurso() {
        return repository.findAllByCurso((byte) 1);
    }

    public List<ClaseChequera> findAllByTitulo() {
        return repository.findAllByTitulo((byte) 1);
    }

}
