/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Documento;

/**
 * @author daniel
 *
 */
@Repository
public interface IDocumentoRepository extends JpaRepository<Documento, Integer> {

	public Optional<Documento> findByDocumentoId(Integer documentoId);

}
