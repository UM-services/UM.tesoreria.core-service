/**
 * 
 */
package um.tesoreria.core.repository;

import com.jayway.jsonpath.JsonPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ChequeraImpresionCabecera;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author daniel
 *
 */
@Repository
public interface ChequeraImpresionCabeceraRepository extends JpaRepository<ChequeraImpresionCabecera, Long> {

    Optional<ChequeraImpresionCabecera> findFirstByFacultadIdAndTipoChequeraIdAndChequeraSerieIdOrderByChequeraImpresionCabeceraIdDesc(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

    List<ChequeraImpresionCabecera> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdOrderByChequeraImpresionCabeceraIdDesc(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

    void deleteAllByChequeraImpresionCabeceraIdIn(Collection<Long> chequeraImpresionCabeceraIds);

}
