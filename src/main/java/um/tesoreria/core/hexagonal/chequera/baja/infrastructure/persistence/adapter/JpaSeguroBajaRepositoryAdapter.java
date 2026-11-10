package um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.baja.domain.ports.out.SeguroBajaRepository;
import um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.repository.JpaSeguroRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class JpaSeguroBajaRepositoryAdapter implements SeguroBajaRepository {

    private final JpaSeguroRepository repository;

    @Override
    public void resetBaja(Integer documentoId, BigDecimal personaId, Integer lectivoId) {
        repository.findByDocumentoIdAndPersonaIdAndLectivoId(documentoId, personaId, lectivoId)
                .ifPresent(seguro -> {
                    seguro.setBaja((byte) 0);
                    repository.save(seguro);
                });
    }

}
