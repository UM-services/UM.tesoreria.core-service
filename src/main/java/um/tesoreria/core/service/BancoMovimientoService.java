package um.tesoreria.core.service;

import um.tesoreria.core.exception.BancoMovimientoException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.BancoMovimiento;
import um.tesoreria.core.kotlin.repository.BancoMovimientoRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BancoMovimientoService {

    private final BancoMovimientoRepository repository;

    public BancoMovimientoService(BancoMovimientoRepository repository) {
        this.repository = repository;
    }

    public BancoMovimiento findByValorMovimientoId(Long valorMovimientoId) {
        return Objects.requireNonNull(repository.findByValorMovimientoId(valorMovimientoId)).orElseThrow(BancoMovimientoException::new);
    }

    public BancoMovimiento add(BancoMovimiento bancoMovimiento) {
        bancoMovimiento = repository.save(bancoMovimiento);
        return bancoMovimiento;
    }

    public BancoMovimiento update(BancoMovimiento newBancoMovimiento, Long bancoMovimientoId) {
        return repository.findByBancoMovimientoId(bancoMovimientoId).map(bancoMovimiento -> {
            bancoMovimiento = new BancoMovimiento(bancoMovimientoId, newBancoMovimiento.getBancariaId(), newBancoMovimiento.getFecha(), newBancoMovimiento.getOrden(), newBancoMovimiento.getTipo(), newBancoMovimiento.getNumeroComprobante(), newBancoMovimiento.getConcepto(), newBancoMovimiento.getImporte(), newBancoMovimiento.getNumeroCuenta(), newBancoMovimiento.getDebita(), newBancoMovimiento.getFechaConciliacion(), newBancoMovimiento.getAnulado(), newBancoMovimiento.getFechaContable(), newBancoMovimiento.getOrdenContable(), newBancoMovimiento.getValorMovimientoId(), null, null);
            return repository.save(bancoMovimiento);
        }).orElseThrow(() -> new BancoMovimientoException(bancoMovimientoId));
    }

    @Transactional
    public void deleteByBancoMovimientoId(Long bancoMovimientoId) {
        repository.deleteByBancoMovimientoId(bancoMovimientoId);
    }

    @Transactional
    public void deleteAllByBancoMovimientoIdIn(List<Long> bancoMovimientoIds) {
        log.debug("Processing deleteAllByBancoMovimientoIdIn");
        repository.deleteAllByBancoMovimientoIdIn(bancoMovimientoIds);
    }


    public BancoMovimiento generateNextId(Long bancariaId, OffsetDateTime fecha) {
        BancoMovimiento bancoMovimiento = Objects.requireNonNull(repository.findFirstByBancariaIdAndFechaOrderByOrdenDesc(bancariaId, fecha)).orElse(new BancoMovimiento());
        return new BancoMovimiento(null, bancariaId, fecha, bancoMovimiento.getOrden() + 1, "", "", "", BigDecimal.ZERO, null, (byte) 0, null, (byte) 0, null, 0, null, null, null);
    }

}
