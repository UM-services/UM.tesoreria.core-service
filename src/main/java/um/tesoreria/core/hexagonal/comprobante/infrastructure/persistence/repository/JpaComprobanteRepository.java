/**
 * 
 */
package um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.entity.ComprobanteEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaComprobanteRepository extends JpaRepository<ComprobanteEntity, Integer> {

	public List<ComprobanteEntity> findAllByTipoTransaccionId(Integer tipoTransaccionId);

	public List<ComprobanteEntity> findAllByOrdenPago(Byte ordenPago);

	public List<ComprobanteEntity> findAllByOrdenPagoAndTipoTransaccionId(byte b, Integer tipoTransaccionId);

	public Optional<ComprobanteEntity> findFirstByTipoTransaccionId(Integer tipoTransaccionId);

	public Optional<ComprobanteEntity> findByComprobanteId(Integer comprobanteId);

}
