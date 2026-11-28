/**
 *
 */
package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ChequeraImpresionDetalle;

import java.util.Collection;
import java.util.List;

/**
 * @author daniel
 *
 */
@Repository
public interface ChequeraImpresionDetalleRepository extends JpaRepository<ChequeraImpresionDetalle, Long> {

    List<ChequeraImpresionDetalle> findAllByChequeraImpresionCabeceraId(Long chequeraImpresionCabeceraId);

    void deleteAllByChequeraImpresionCabeceraIdIn(Collection<Long> chequeraImpresionCabeceraIds);

}
