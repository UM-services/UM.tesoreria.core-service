package um.tesoreria.core.service;

import um.tesoreria.core.kotlin.model.ProveedorPago;
import um.tesoreria.core.repository.IProveedorPagoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorPagoService {

    private final IProveedorPagoRepository repository;

    public ProveedorPagoService(IProveedorPagoRepository repository) {
        this.repository = repository;
    }

    public List<ProveedorPago> findAllByPago(Long proveedorMovimientoId) {
        return repository.findAllByProveedorMovimientoIdPago(proveedorMovimientoId);
    }

    public List<ProveedorPago> findAllByFactura(Long proveedorMovimientoId) {
        return repository.findAllByProveedorMovimientoIdFactura(proveedorMovimientoId);
    }

    @Transactional
    public void delete(Long proveedorPagoId) {
        repository.deleteByProveedorPagoId(proveedorPagoId);
    }

    @Transactional
    public void deleteAllByProveedorPagoIdIn(List<Long> proveedorPagoIds) {
        repository.deleteAllByProveedorPagoIdIn(proveedorPagoIds);
    }

}
