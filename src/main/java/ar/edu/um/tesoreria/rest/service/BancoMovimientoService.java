package ar.edu.um.tesoreria.rest.service;

import ar.edu.um.tesoreria.rest.exception.BancoMovimientoException;
import ar.edu.um.tesoreria.rest.kotlin.model.BancoMovimiento;
import ar.edu.um.tesoreria.rest.kotlin.repository.IBancoMovimientoRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@Slf4j
public class BancoMovimientoService {

    @Autowired
    private IBancoMovimientoRepository repository;

    public BancoMovimiento findByValorMovimientoId(Long valorMovimientoId) {
        return repository.findByValorMovimientoId(valorMovimientoId).orElseThrow(() -> new BancoMovimientoException());
    }

    public BancoMovimiento add(BancoMovimiento bancoMovimiento) {
        bancoMovimiento = repository.save(bancoMovimiento);
        return bancoMovimiento;
    }

    public BancoMovimiento update(BancoMovimiento newBancoMovimiento, Long bancoMovimientoId) {
        return repository.findByBancoMovimientoId(bancoMovimientoId).map(bancoMovimiento -> {
            bancoMovimiento = new BancoMovimiento(bancoMovimientoId, newBancoMovimiento.getBancariaId(), newBancoMovimiento.getFecha(), newBancoMovimiento.getOrden(), newBancoMovimiento.getTipo(), newBancoMovimiento.getNumeroComprobante(), newBancoMovimiento.getConcepto(), newBancoMovimiento.getImporte(), newBancoMovimiento.getNumeroCuenta(), newBancoMovimiento.getDebita(), newBancoMovimiento.getFechaConciliacion(), newBancoMovimiento.getAnulado(), newBancoMovimiento.getFechaContable(), newBancoMovimiento.getOrdenContable(), newBancoMovimiento.getValorMovimientoId(), null, null);
            bancoMovimiento = repository.save(bancoMovimiento);
            return bancoMovimiento;
        }).orElseThrow(() -> new BancoMovimientoException(bancoMovimientoId));
    }

    @Transactional
    public void deleteByBancoMovimientoId(Long bancoMovimientoId) {
        repository.deleteByBancoMovimientoId(bancoMovimientoId);
    }

    public BancoMovimiento generateNextId(Long bancariaId, OffsetDateTime fecha) {
        log.debug("a");
        BancoMovimiento bancoMovimiento = repository.findFirstByBancariaIdAndFechaOrderByOrdenDesc(bancariaId, fecha).orElse(new BancoMovimiento());
        log.debug("b");
        bancoMovimiento = new BancoMovimiento(null, bancariaId, fecha, bancoMovimiento.getOrden() + 1, "", "", "", BigDecimal.ZERO, null, (byte) 0, null, (byte) 0, null, 0, null, null, null);
        log.debug("c");
        return bancoMovimiento;
    }

}
