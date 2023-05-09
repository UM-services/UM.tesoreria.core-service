/**
 *
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.ProveedorValor;
import ar.edu.um.tesoreria.rest.kotlin.model.ValorMovimiento;
import ar.edu.um.tesoreria.rest.kotlin.repository.IValorMovimientoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ValorMovimientoException;

/**
 * @author daniel
 *
 */
@Service
public class ValorMovimientoService {

    @Autowired
    private IValorMovimientoRepository repository;

    @Autowired
    private ProveedorValorService proveedorValorService;

    public List<ValorMovimiento> findAllByValorMovimientoIdIn(List<Long> valorMovimientoIds) {
        return repository.findAllByValorMovimientoIdIn(valorMovimientoIds);
    }

    public List<ValorMovimiento> findAllByOrdenPago(Long proveedorMovimientoId) {
        List<Long> valorMovimientoIds = proveedorValorService.findAllByProveedorMovimientoId(proveedorMovimientoId).stream().map(ProveedorValor::getValorMovimientoId).toList();
        return repository.findAllByValorMovimientoIdInAndEstadoNotAndEstadoNot(valorMovimientoIds, "Anulado", "Reemplazado", Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending()));
    }

    public ValorMovimiento findByValorMovimientoId(Long valorMovimientoId) {
        return repository.findByValorMovimientoId(valorMovimientoId).orElseThrow(() -> new ValorMovimientoException(valorMovimientoId));
    }

    public ValorMovimiento findByNumero(Integer valorId, Long numero) {
        return repository.findFirstByValorIdAndNumero(valorId, numero)
                .orElseThrow(() -> new ValorMovimientoException(valorId, numero));
    }

    public ValorMovimiento findByBanco(Integer valorId, Long numero, Long bancariaId) {
        return repository.findFirstByValorIdAndNumeroAndBancariaIdOrigen(valorId, numero, bancariaId)
                .orElseThrow(() -> new ValorMovimientoException(valorId, numero, bancariaId));
    }

    public ValorMovimiento findFirstByContable(OffsetDateTime fechaContable, Integer ordenContable) {
        return repository.findFirstByFechaContableAndOrdenContable(fechaContable, ordenContable)
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

}
