/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.LectivoTotalImputacion;

/**
 * @author daniel
 *
 */
@Repository
public interface LectivoTotalImputacionRepository extends JpaRepository<LectivoTotalImputacion, Long> {

	public List<LectivoTotalImputacion> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId,
			Integer lectivoId, Integer tipoChequeraId);

	public Optional<LectivoTotalImputacion> findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoId(
			Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId);

	public Optional<LectivoTotalImputacion> findByLectivoTotalImputacionId(Long lectivoTotalImputacionId);

}
