/**
 * 
 */
package um.tesoreria.core.hexagonal.documento.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.documento.infrastructure.persistence.entity.DocumentoEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaDocumentoRepository extends JpaRepository<DocumentoEntity, Integer> {

	Optional<DocumentoEntity> findByDocumentoId(Integer documentoId);

}
