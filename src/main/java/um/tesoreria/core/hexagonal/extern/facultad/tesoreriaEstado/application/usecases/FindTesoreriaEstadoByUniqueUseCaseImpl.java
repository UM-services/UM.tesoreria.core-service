package um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.model.TesoreriaEstadoFacultad;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.ports.in.FindTesoreriaEstadoByUniqueUseCase;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.ports.out.TesoreriaEstadoRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindTesoreriaEstadoByUniqueUseCaseImpl implements FindTesoreriaEstadoByUniqueUseCase {

    private final TesoreriaEstadoRepository tesoreriaEstadoRepository;

    @Override
    public Optional<TesoreriaEstadoFacultad> findByUnique(Integer facultadId, BigDecimal personaId, Integer documentoId) {
        return tesoreriaEstadoRepository.findByUnique(facultadId, personaId, documentoId);
    }
}
