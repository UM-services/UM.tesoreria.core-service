package um.tesoreria.core.hexagonal.facultad.application.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.FacultadException;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.facultad.domain.ports.in.*;
import um.tesoreria.core.model.view.FacultadLectivo;
import um.tesoreria.core.model.view.FacultadLectivoSede;
import um.tesoreria.core.model.view.FacultadPersona;
import um.tesoreria.core.repository.view.FacultadLectivoRepository;
import um.tesoreria.core.repository.view.FacultadLectivoSedeRepository;
import um.tesoreria.core.repository.view.FacultadPersonaRepository;

@Service
@RequiredArgsConstructor
public class FacultadService {
    
    // Hexagonal Ports
    private final GetAllFacultadesUseCase getAllFacultadesUseCase;
    private final GetFacultadesFiltradasUseCase getFacultadesFiltradasUseCase;
    private final GetFacultadByIdUseCase getFacultadByIdUseCase;
    
    // Legacy View Repositories (Will be refactored to their own slices later)
    private final FacultadLectivoSedeRepository facultadLectivoSedeRepository;
    private final FacultadPersonaRepository facultadPersonaRepository;
    private final FacultadLectivoRepository facultadLectivoRepository;

    public List<Facultad> findAll() {
        return getAllFacultadesUseCase.getAll();
    }

    public List<Facultad> findFacultades() {
        return getFacultadesFiltradasUseCase.getFacultadesFiltradas();
    }

    public Facultad findByFacultadId(Integer facultadId) {
        return getFacultadByIdUseCase.getById(facultadId)
                .orElseThrow(() -> new FacultadException(facultadId));
    }

    // Legacy logic wrapped for now
    public List<FacultadLectivo> findAllByLectivoId(Integer lectivoId) {
        Integer[] facultades = { 1, 2, 3, 4, 5, 6, 14, 15 };
        return facultadLectivoRepository.findAllByLectivoIdAndFacultadIdIn(lectivoId, Arrays.asList(facultades));
    }

    public List<FacultadPersona> findAllByPersona(BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        return facultadPersonaRepository.findAllByPersonaIdAndDocumentoIdAndLectivoId(personaId, documentoId, lectivoId);
    }

    public List<FacultadLectivoSede> findAllByDisenho(Integer lectivoId, Integer geograficaId) {
        if (geograficaId == 0)
            return facultadLectivoSedeRepository.findAllByLectivoId(lectivoId);
        return facultadLectivoSedeRepository.findAllByLectivoIdAndGeograficaId(lectivoId, geograficaId);
    }
}
