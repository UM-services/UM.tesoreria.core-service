package ar.edu.um.tesoreria.rest.service;

import ar.edu.um.tesoreria.rest.exception.BancoMovimientoException;
import ar.edu.um.tesoreria.rest.kotlin.model.BancoMovimiento;
import ar.edu.um.tesoreria.rest.repository.IBancoMovimientoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BancoMovimientoService {

    @Autowired
    private IBancoMovimientoRepository repository;

    public BancoMovimiento findByValorMovimientoId(Long valorMovimientoId) {
        return repository.findByValorMovimientoId(valorMovimientoId).orElseThrow(() -> new BancoMovimientoException());
    }

    @Transactional
    public void deleteByBancoMovimientoId(Long bancoMovimientoId) {
        repository.deleteByBancoMovimientoId(bancoMovimientoId);
    }
}
