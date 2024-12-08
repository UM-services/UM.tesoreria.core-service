/**
 *
 */
package um.tesoreria.core.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ValorMovimientoException;
import um.tesoreria.core.kotlin.model.ProveedorValor;
import um.tesoreria.core.kotlin.model.ValorMovimiento;
import um.tesoreria.core.kotlin.repository.ValorMovimientoRepository;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ValorMovimientoService {

    private final ValorMovimientoRepository repository;
    private final ProveedorValorService proveedorValorService;

    public ValorMovimientoService(ValorMovimientoRepository repository, ProveedorValorService proveedorValorService) {
        this.repository = repository;
        this.proveedorValorService = proveedorValorService;
    }

    public List<ValorMovimiento> findAllByValorMovimientoIdIn(List<Long> valorMovimientoIds) {
        return repository.findAllByValorMovimientoIdIn(valorMovimientoIds);
    }

    public List<ValorMovimiento> findAllByOrdenPago(Long proveedorMovimientoId) {
        List<Long> valorMovimientoIds = proveedorValorService.findAllByProveedorMovimientoId(proveedorMovimientoId).stream().map(ProveedorValor::getValorMovimientoId).toList();
        return repository.findAllByValorMovimientoIdInAndEstadoNotAndEstadoNot(valorMovimientoIds, "Anulado", "Reemplazado", Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending()));
    }

    public ValorMovimiento findByValorMovimientoId(Long valorMovimientoId) {
        return Objects.requireNonNull(repository.findByValorMovimientoId(valorMovimientoId)).orElseThrow(() -> new ValorMovimientoException(valorMovimientoId));
    }

    public ValorMovimiento findByNumero(Integer valorId, Long numero) {
        return Objects.requireNonNull(repository.findFirstByValorIdAndNumero(valorId, numero))
                .orElseThrow(() -> new ValorMovimientoException(valorId, numero));
    }

    public ValorMovimiento findByBanco(Integer valorId, Long numero, Long bancariaId) {
        return Objects.requireNonNull(repository.findFirstByValorIdAndNumeroAndBancariaIdOrigen(valorId, numero, bancariaId))
                .orElseThrow(() -> new ValorMovimientoException(valorId, numero, bancariaId));
    }

    public ValorMovimiento findFirstByContable(OffsetDateTime fechaContable, Integer ordenContable) {
        return Objects.requireNonNull(repository.findFirstByFechaContableAndOrdenContable(fechaContable, ordenContable))
                .orElseThrow(() -> new ValorMovimientoException(fechaContable, ordenContable));
    }

    public ValorMovimiento add(ValorMovimiento valorMovimiento) {
        valorMovimiento = repository.save(valorMovimiento);
        return valorMovimiento;
    }

    public ValorMovimiento update(ValorMovimiento newValorMovimiento, Long valorMovimientoId) {
        return repository.findByValorMovimientoId(valorMovimientoId).map(valorMovimiento -> {
            valorMovimiento = new ValorMovimiento(valorMovimientoId, newValorMovimiento.getValorId(), newValorMovimiento.getFechaEmision(), newValorMovimiento.getNumero(), newValorMovimiento.getBancariaIdOrigen(), newValorMovimiento.getFechaEfectivizacion(), newValorMovimiento.getImporte(), newValorMovimiento.getEstado(), newValorMovimiento.getNombreTitular(), newValorMovimiento.getCuitTitular(), newValorMovimiento.getReemplazo(), newValorMovimiento.getFechaContable(), newValorMovimiento.getOrdenContable(), newValorMovimiento.getFechaContableAnulacion(), newValorMovimiento.getOrdenContableAnulacion(), newValorMovimiento.getLetras(), null, null);
            valorMovimiento = repository.save(valorMovimiento);
            return valorMovimiento;
        }).orElseThrow(() -> new ValorMovimientoException(valorMovimientoId));
    }

    @Transactional
    public void deleteByValorMovimientoId(Long valorMovimientoId) {
        repository.deleteByValorMovimientoId(valorMovimientoId);
    }

    public void deleteAllByValorMovimientoIdIn(List<Long> valorMovimientoIds) {
        log.debug("Processing deleteAllByValorMovimientoIdIn");
        repository.deleteAllByValorMovimientoIdIn(valorMovimientoIds);
    }

}
