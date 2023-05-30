/**
 * 
 */
package um.tesoreria.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.LectivoCuota;

/**
 * @author daniel
 *
 */
@Repository
public interface ILectivoCuotaRepository extends JpaRepository<LectivoCuota, Long> {

	public List<LectivoCuota> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId, Integer lectivoId,
			Integer tipoChequeraId);

	public List<LectivoCuota> findAllByFacultadIdAndLectivoIdAndTipoChequeraIdAndAlternativaId(Integer facultadId,
			Integer lectivoId, Integer tipoChequeraId, Integer alternativaId);

}
