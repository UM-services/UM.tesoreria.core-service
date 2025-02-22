/**
 *
 */
package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ClaseChequera;

import java.util.List;

/**
 * @author daniel
 */
@Repository
public interface ClaseChequeraRepository extends JpaRepository<ClaseChequera, Integer> {

    public List<ClaseChequera> findAllByPosgrado(Byte posgrado);

    public List<ClaseChequera> findAllByCurso(Byte curso);

    public List<ClaseChequera> findAllByTitulo(Byte titulo);

}
