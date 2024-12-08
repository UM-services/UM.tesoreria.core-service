/**
 *
 */
package um.tesoreria.core.service;

import java.util.List;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ProveedorValorException;
import um.tesoreria.core.repository.IProveedorValorRepository;
import um.tesoreria.core.kotlin.model.ProveedorValor;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ProveedorValorService {

    private final IProveedorValorRepository repository;

    public ProveedorValorService(IProveedorValorRepository repository) {
        this.repository = repository;
    }

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
                .orElseThrow(ProveedorValorException::new);
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

    @Transactional
    public void deleteAllByProveedorValorIdIn(List<Long> proveedorValorIds) {
        log.debug("Processing deleteAllByProveedorValorIdIn");
        repository.deleteAllByProveedorValorIdIn(proveedorValorIds);
    }

}
