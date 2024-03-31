/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.LectivoAlternativa;

/**
 * @author daniel
 *
 */
@Repository
public interface ILectivoAlternativaRepository extends JpaRepository<LectivoAlternativa, Long> {

	public List<LectivoAlternativa> findAllByFacultadIdAndLectivoIdAndTipoChequeraIdAndAlternativaId(Integer facultadId,
			Integer lectivoId, Integer tipoChequeraId, Integer alternativaId);

	public Optional<LectivoAlternativa> findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaId(
			Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId);

}
