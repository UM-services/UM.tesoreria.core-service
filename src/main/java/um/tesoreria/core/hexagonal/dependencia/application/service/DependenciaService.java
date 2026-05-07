package um.tesoreria.core.hexagonal.dependencia.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.dependencia.domain.model.Dependencia;
import um.tesoreria.core.hexagonal.dependencia.domain.ports.in.*;
import um.tesoreria.core.exception.DependenciaException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DependenciaService {
    private final GetAllDependenciasUseCase getAllDependenciasUseCase;
    private final GetDependenciaByIdUseCase getDependenciaByIdUseCase;
    private final UpdateDependenciaUseCase updateDependenciaUseCase;

    public List<Dependencia> findAll() { return getAllDependenciasUseCase.getAll(); }
    public Dependencia findByDependenciaId(Integer id) { return getDependenciaByIdUseCase.getById(id).orElseThrow(() -> new DependenciaException(id)); }
    public Dependencia update(Integer id, Dependencia data) { return updateDependenciaUseCase.update(id, data); }
}
