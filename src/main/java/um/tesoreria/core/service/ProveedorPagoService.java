package um.tesoreria.core.service;

import um.tesoreria.core.kotlin.model.ProveedorPago;
import um.tesoreria.core.repository.IProveedorPagoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorPagoService {

    @Autowired
    private IProveedorPagoRepository repository;

    public List<ProveedorPago> findAllByPago(Long proveedorMovimientoId) {
        return repository.findAllByProveedorMovimientoIdPago(proveedorMovimientoId);
    }

    @Transactional
    public void delete(Long proveedorPagoId) {
        repository.deleteByProveedorPagoId(proveedorPagoId);
    }
}
