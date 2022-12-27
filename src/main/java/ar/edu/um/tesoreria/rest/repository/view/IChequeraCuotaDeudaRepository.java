/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.ChequeraCuotaDeuda;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraCuotaDeudaRepository extends JpaRepository<ChequeraCuotaDeuda, Long> {

	public List<ChequeraCuotaDeuda> findAllByVencimiento1Between(OffsetDateTime desde, OffsetDateTime hasta,
			Pageable pageable);

}
