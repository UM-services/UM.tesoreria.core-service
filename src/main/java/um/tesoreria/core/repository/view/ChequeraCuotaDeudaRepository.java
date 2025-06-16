/**
 *
 */
package um.tesoreria.core.repository.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.view.ChequeraCuotaDeuda;

/**
 * @author daniel
 */
@Repository
public interface ChequeraCuotaDeudaRepository extends JpaRepository<ChequeraCuotaDeuda, Long> {

    List<ChequeraCuotaDeuda> findAllByVencimiento1Between(OffsetDateTime desde, OffsetDateTime hasta,
                                                          Pageable pageable);

    List<ChequeraCuotaDeuda> findAllByVencimiento1BetweenAndFacultadIdIn(OffsetDateTime offsetDateTime, OffsetDateTime offsetDateTime1, List<Integer> facultadIds, Pageable pageable);

    List<ChequeraCuotaDeuda> findAllByVencimiento1BetweenAndChequeraIdIn(OffsetDateTime offsetDateTime, OffsetDateTime offsetDateTime1, Pageable pageable, List<Long> chequeraIds);

    List<ChequeraCuotaDeuda> findAllByTipoChequeraIdInAndVencimiento1Between(List<Integer> tipoChequeraIds, OffsetDateTime desde, OffsetDateTime hasta);

}
