package um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface BorradorChequeraRepository {

    BorradorChequera save(BorradorChequera borrador);

    Optional<BorradorChequera> findById(Long borradorId);

    Optional<BorradorChequera> findByClaveIdempotencia(String claveIdempotencia);

    List<BorradorChequera> findAllByUsuarioIdAndEstado(String usuarioId, EstadoBorrador estado);

    void deleteById(Long borradorId);

    int descartarAnterioresA(OffsetDateTime limite);
}
