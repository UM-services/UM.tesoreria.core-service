package ar.edu.um.tesoreria.rest.service;

import ar.edu.um.tesoreria.rest.kotlin.model.ProveedorPago;
import ar.edu.um.tesoreria.rest.repository.IProveedorPagoRepository;
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
