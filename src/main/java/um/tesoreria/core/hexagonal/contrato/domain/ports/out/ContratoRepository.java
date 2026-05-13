package um.tesoreria.core.hexagonal.contrato.domain.ports.out;

import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ContratoRepository {
    Contrato create(Contrato contrato);
    Optional<Contrato> findById(Long id);
    List<Contrato> findAllByFacultad(Integer facultadId, Integer geograficaId);
    List<Contrato> findAllAjustable(OffsetDateTime referencia);
    List<Contrato> findAllVigente(OffsetDateTime referencia);
    List<Contrato> findAllByPersona(BigDecimal personaId, Integer documentoId);
    Optional<Contrato> update(Long id, Contrato contrato);
    List<Contrato> saveAll(List<Contrato> contratos);
    boolean deleteById(Long id);
}
