/**
 *
 */
package um.tesoreria.core.hexagonal.chequera.claseChequera.application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.entity.ClaseChequeraEntity;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.repository.JpaClaseChequeraRepository;

/**
 * @author daniel
 */
@Service
public class ClaseChequeraService {

    @Autowired
    private JpaClaseChequeraRepository repository;

    public List<ClaseChequeraEntity> findAll() {
        return repository.findAll();
    }

    public List<ClaseChequeraEntity> findAllByPosgrado() {
        return repository.findAllByPosgrado((byte) 1);
    }

    public List<ClaseChequeraEntity> findAllByCurso() {
        return repository.findAllByCurso((byte) 1);
    }

    public List<ClaseChequeraEntity> findAllByTitulo() {
        return repository.findAllByTitulo((byte) 1);
    }

}
