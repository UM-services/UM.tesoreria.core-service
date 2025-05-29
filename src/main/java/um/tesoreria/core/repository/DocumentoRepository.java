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
public interface DocumentoRepository extends JpaRepository<Documento, Integer> {

	Optional<Documento> findByDocumentoId(Integer documentoId);

}
