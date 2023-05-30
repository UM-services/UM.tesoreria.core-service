/**
 * 
 */
package um.tesoreria.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.ClaseChequera;

import java.util.List;

/**
 * @author daniel
 *
 */
@Repository
public interface IClaseChequeraRepository extends JpaRepository<ClaseChequera, Integer> {

    public List<ClaseChequera> findALlByPosgrado(Byte posgrado);

}
