/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Comprobante;

/**
 * @author daniel
 *
 */
@Repository
public interface IComprobanteRepository extends JpaRepository<Comprobante, Integer> {

	public List<Comprobante> findAllByTipoTransaccionId(Integer tipoTransaccionId);

	public Optional<Comprobante> findByComprobanteId(Integer comprobanteId);

}
