/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IArticuloRepository extends JpaRepository<Articulo, Long> {

	public Optional<Articulo> findByArticuloId(Long articuloId);

	public Optional<Articulo> findTopByOrderByArticuloIdDesc();

}
