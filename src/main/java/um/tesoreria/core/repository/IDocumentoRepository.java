/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.Documento;

/**
 * @author daniel
 *
 */
@Repository
public interface IDocumentoRepository extends JpaRepository<Documento, Integer> {

	public Optional<Documento> findByDocumentoId(Integer documentoId);

}
