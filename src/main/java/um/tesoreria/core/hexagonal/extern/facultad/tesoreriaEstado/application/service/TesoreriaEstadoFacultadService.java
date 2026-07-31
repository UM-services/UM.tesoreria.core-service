package um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.application.exception.TesoreriaEstadoException;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.model.TesoreriaEstadoFacultad;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.ports.in.FindTesoreriaEstadoByUniqueUseCase;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TesoreriaEstadoFacultadService {

    private final FindTesoreriaEstadoByUniqueUseCase findTesoreriaEstadoByUniqueUseCase;

    public TesoreriaEstadoFacultad findByUnique(Integer facultadId, BigDecimal personaId, Integer documentoId) {
        return findTesoreriaEstadoByUniqueUseCase.findByUnique(facultadId, personaId, documentoId)
                .orElseThrow(() -> new TesoreriaEstadoException(facultadId, personaId, documentoId));
    }
}
