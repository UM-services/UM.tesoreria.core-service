/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.LectivoTotal;

/**
 * @author daniel
 *
 */
@Repository
public interface LectivoTotalRepository extends JpaRepository<LectivoTotal, Long> {

	public List<LectivoTotal> findAllByFacultadId(Integer facultadId);

	public List<LectivoTotal> findAllByFacultadIdAndLectivoId(Integer facultadId, Integer lectivoId);

	public List<LectivoTotal> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId, Integer lectivoId,
			Integer tipoChequeraId);

	public Optional<LectivoTotal> findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoId(Integer facultadId,
			Integer lectivoId, Integer tipoChequeraId, Integer productoId);

}
