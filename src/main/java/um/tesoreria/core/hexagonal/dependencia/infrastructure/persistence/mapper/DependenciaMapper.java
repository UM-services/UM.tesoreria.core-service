package um.tesoreria.core.hexagonal.dependencia.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.mapper.CuentaMapper;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.mapper.GeograficaMapper;
import um.tesoreria.core.hexagonal.dependencia.domain.model.Dependencia;
import um.tesoreria.core.hexagonal.dependencia.infrastructure.persistence.entity.DependenciaEntity;

@Component
@RequiredArgsConstructor
public class DependenciaMapper {
    private final GeograficaMapper geograficaMapper;
    private final CuentaMapper cuentaMapper;

    public Dependencia toDomain(DependenciaEntity entity) {
        if (entity == null) return null;
        return Dependencia.builder()
                .dependenciaId(entity.getDependenciaId())
                .nombre(entity.getNombre())
                .acronimo(entity.getAcronimo())
                .facultadId(entity.getFacultadId())
                .geograficaId(entity.getGeograficaId())
                .cuentaHonorariosPagar(entity.getCuentaHonorariosPagar())
                .facultad(entity.getFacultad())
                .geografica(geograficaMapper.toDomainModel(entity.getGeografica()))
                .cuenta(cuentaMapper.toDomain(entity.getCuenta()))
                .build();
    }

    public DependenciaEntity toEntity(Dependencia domain) {
        if (domain == null) return null;
        DependenciaEntity.DependenciaEntityBuilder builder = DependenciaEntity.builder()
                .dependenciaId(domain.getDependenciaId())
                .facultadId(domain.getFacultadId())
                .geograficaId(domain.getGeograficaId())
                .cuentaHonorariosPagar(domain.getCuentaHonorariosPagar());
        
        if (domain.getNombre() != null) builder.nombre(domain.getNombre());
        if (domain.getAcronimo() != null) builder.acronimo(domain.getAcronimo());
        
        return builder.build();
    }
}
