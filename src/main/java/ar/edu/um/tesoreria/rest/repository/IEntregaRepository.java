/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Entrega;

/**
 * @author daniel
 *
 */
@Repository
public interface IEntregaRepository extends JpaRepository<Entrega, Long> {

	public List<Entrega> findAllByEntregaIdIn(List<Long> entregaIds, Sort sort);

	public List<Entrega> findAllByEntregaIdInAndAnulada(List<Long> entregaIds, Byte anulada, Sort sort);

	public Optional<Entrega> findByEntregaId(Long entregaId);

	@Modifying
	public void deleteByEntregaId(Long entregaId);

}
