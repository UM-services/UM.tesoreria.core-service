/**
 *
 */
package um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.ComprobanteException;
import um.tesoreria.rest.kotlin.model.Comprobante;
import um.tesoreria.rest.repository.IComprobanteRepository;
import um.tesoreria.rest.exception.ComprobanteException;
import um.tesoreria.rest.repository.IComprobanteRepository;

/**
 * @author daniel
 *
 */
@Service
public class ComprobanteService {

    @Autowired
    private IComprobanteRepository repository;

    public List<Comprobante> findAll() {
        return repository.findAll();
    }

    public List<Comprobante> findAllByOrdenPago() {
        return repository.findAllByOrdenPago((byte) 1);
    }

    public List<Comprobante> findAllByTipoTransaccionId(Integer tipoTransaccionId) {
        return repository.findAllByTipoTransaccionId(tipoTransaccionId);
    }

    public List<Comprobante> findAllByOrdenPagoAndTipoTransaccionId(Integer tipoTransaccionId) {
        return repository.findAllByOrdenPagoAndTipoTransaccionId((byte) 1, tipoTransaccionId);
    }

    public Comprobante findByTipoTransaccionId(Integer tipoTransaccionId) {
        return repository.findFirstByTipoTransaccionId(tipoTransaccionId).orElseThrow(() -> new ComprobanteException());
    }

    public Comprobante findByComprobanteId(Integer comprobanteId) {
        return repository.findByComprobanteId(comprobanteId)
                .orElseThrow(() -> new ComprobanteException(comprobanteId));
    }

}
