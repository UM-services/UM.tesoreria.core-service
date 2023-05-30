/**
 * 
 */
package um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.Postal;

/**
 * @author daniel
 *
 */
@Repository
public interface IPostalRepository extends JpaRepository<Postal, Integer> {

	public Optional<Postal> findByCodigopostal(Integer codigopostal);

}
