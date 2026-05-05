/**
 * 
 */
package um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.entity.ArticuloKey;

/**
 * @author daniel
 *
 */
@Repository
public interface ArticuloKeyRepository extends JpaRepository<ArticuloKey, Long>, ArticuloKeyRepositoryCustom {

}
