package ar.edu.um.tesoreria.rest.repository;

import ar.edu.um.tesoreria.rest.kotlin.model.BancoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IBancoMovimientoRepository extends JpaRepository<BancoMovimiento, Long> {

    public Optional<BancoMovimiento> findByValorMovimientoId(Long valorMovimientoId);

    @Modifying
    public void deleteByBancoMovimientoId(Long bancoMovimientoId);
}
