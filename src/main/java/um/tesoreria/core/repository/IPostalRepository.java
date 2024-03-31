/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.Postal;

/**
 * @author daniel
 *
 */
@Repository
public interface IPostalRepository extends JpaRepository<Postal, Integer> {

	public Optional<Postal> findByCodigopostal(Integer codigopostal);

}
