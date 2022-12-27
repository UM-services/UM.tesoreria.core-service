/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.PayPerTic;

/**
 * @author daniel
 *
 */
@Repository
public interface IPayPerTicRepository extends JpaRepository<PayPerTic, String> {

	public List<PayPerTic> findAllByPaymentdateBetween(OffsetDateTime desde, OffsetDateTime hasta);

	public Optional<PayPerTic> findByPayperticId(String payperticId);

}
