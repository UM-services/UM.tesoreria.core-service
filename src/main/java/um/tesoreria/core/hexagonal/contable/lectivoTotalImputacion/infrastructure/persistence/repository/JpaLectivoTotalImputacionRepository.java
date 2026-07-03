/**
 * 
 */
package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.entity.LectivoTotalImputacionEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaLectivoTotalImputacionRepository extends JpaRepository<LectivoTotalImputacionEntity, Long> {

	List<LectivoTotalImputacionEntity> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId,
	                                                                                           Integer lectivoId, Integer tipoChequeraId);

	List<LectivoTotalImputacionEntity> findAllByLectivoId(Integer lectivoId);

	Optional<LectivoTotalImputacionEntity> findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoId(
			Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId);

	Optional<LectivoTotalImputacionEntity> findByLectivoTotalImputacionId(Long lectivoTotalImputacionId);

}
