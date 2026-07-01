package um.tesoreria.core.hexagonal.lectivo.domain.ports.out;

import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LectivoRepository {
    List<Lectivo> findAll();
    List<Lectivo> findAllReverse();
    List<Lectivo> findAllByPersona(BigDecimal personaId, Integer documentoId);
    Optional<Lectivo> findById(Integer lectivoId);
    Optional<Lectivo> findByFecha(OffsetDateTime fecha);
    Optional<Lectivo> findLast();
    Lectivo create(Lectivo lectivo);
    void deleteById(Integer lectivoId);
}
