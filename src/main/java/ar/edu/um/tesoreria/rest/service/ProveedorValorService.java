/**
 *
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.ProveedorValor;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ProveedorValorException;
import ar.edu.um.tesoreria.rest.repository.IProveedorValorRepository;

/**
 * @author daniel
 *
 */
@Service
public class ProveedorValorService {

    @Autowired
    private IProveedorValorRepository repository;

    public List<ProveedorValor> findAllByProveedorMovimientoId(Long proveedorMovimientoId) {
        return repository.findAllByProveedorMovimientoId(proveedorMovimientoId, Sort.by("valorMovimiento.valorId").ascending().and(Sort.by("valorMovimiento.numero").ascending()));
    }

    public List<ProveedorValor> findAllByValorMovimientoId(Long valorMovimientoId) {
        return repository.findAllByValorMovimientoId(valorMovimientoId);
    }

    public ProveedorValor findByValorMovimientoId(Long valorMovimientoId) {
        return repository.findFirstByValorMovimientoId(valorMovimientoId)
                .orElseThrow(() -> new ProveedorValorException(valorMovimientoId));
    }

    public ProveedorValor findByProveedorValorId(Long proveedorValorId) {
        return repository.findByProveedorValorId(proveedorValorId)
                .orElseThrow(() -> new ProveedorValorException());
    }

    public ProveedorValor findLastByProveedorMovimientoId(Long proveedorMovimientoId) {
        return repository.findFirstByProveedorMovimientoIdOrderByOrdenDesc(proveedorMovimientoId).orElseThrow(() -> new ProveedorValorException(proveedorMovimientoId));
    }

    public ProveedorValor add(ProveedorValor proveedorValor) {
        proveedorValor = repository.save(proveedorValor);
        return proveedorValor;
    }

    @Transactional
    public void deleteByProveedorValorId(Long proveedorValorId) {
        repository.deleteByProveedorValorId(proveedorValorId);
    }

}
