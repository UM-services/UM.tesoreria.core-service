/**
 * 
 */
package um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.Comprobante;

/**
 * @author daniel
 *
 */
@Repository
public interface IComprobanteRepository extends JpaRepository<Comprobante, Integer> {

	public List<Comprobante> findAllByTipoTransaccionId(Integer tipoTransaccionId);

	public List<Comprobante> findAllByOrdenPago(Byte ordenPago);

	public List<Comprobante> findAllByOrdenPagoAndTipoTransaccionId(byte b, Integer tipoTransaccionId);

	public Optional<Comprobante> findFirstByTipoTransaccionId(Integer tipoTransaccionId);

	public Optional<Comprobante> findByComprobanteId(Integer comprobanteId);

}
