/**
 *
 */
package um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.entity.ClaseChequeraEntity;

import java.util.List;

/**
 * @author daniel
 */
@Repository
public interface JpaClaseChequeraRepository extends JpaRepository<ClaseChequeraEntity, Integer> {

    List<ClaseChequeraEntity> findAllByPosgrado(Byte posgrado);

    List<ClaseChequeraEntity> findAllByCurso(Byte curso);

    List<ClaseChequeraEntity> findAllByTitulo(Byte titulo);

}
