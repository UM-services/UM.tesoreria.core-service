/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.LectivoAlternativa;

/**
 * @author daniel
 *
 */
@Repository
public interface LectivoAlternativaRepository extends JpaRepository<LectivoAlternativa, Long> {

	List<LectivoAlternativa> findAllByFacultadIdAndLectivoIdAndTipoChequeraIdAndAlternativaId(Integer facultadId,
																							  Integer lectivoId, Integer tipoChequeraId, Integer alternativaId);

	Optional<LectivoAlternativa> findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaId(
			Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId);

}
