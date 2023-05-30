/**
 * 
 */
package um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.Producto;

/**
 * @author daniel
 *
 */
@Repository
public interface IProductoRepository extends JpaRepository<Producto, Integer> {

	public Optional<Producto> findByProductoId(Integer productoId);

}
