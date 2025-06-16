/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.LectivoCuota;

/**
 * @author daniel
 *
 */
@Repository
public interface LectivoCuotaRepository extends JpaRepository<LectivoCuota, Long> {

	public List<LectivoCuota> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId, Integer lectivoId,
			Integer tipoChequeraId);

	public List<LectivoCuota> findAllByFacultadIdAndLectivoIdAndTipoChequeraIdAndAlternativaId(Integer facultadId,
			Integer lectivoId, Integer tipoChequeraId, Integer alternativaId);

}
