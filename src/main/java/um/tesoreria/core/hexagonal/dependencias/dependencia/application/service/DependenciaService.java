package um.tesoreria.core.hexagonal.dependencias.dependencia.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.model.Dependencia;
import um.tesoreria.core.exception.DependenciaException;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.ports.in.GetAllDependenciasUseCase;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.ports.in.GetDependenciaByIdUseCase;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.ports.in.UpdateDependenciaUseCase;

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
