/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.LectivoAlternativa;

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
