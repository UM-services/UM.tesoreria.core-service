package um.tesoreria.core.hexagonal.setup.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.setup.domain.model.Setup;
import um.tesoreria.core.hexagonal.setup.infrastructure.persistence.entity.SetupEntity;

@Component
public class SetupMapper {

    public Setup toDomain(SetupEntity entity) {
        if (entity == null) return null;
        return Setup.builder()
                .setupId(entity.getSetupId())
                .cuotasPermitidas(entity.getCuotasPermitidas())
                .cuentaHonorariosPagar(entity.getCuentaHonorariosPagar())
                .build();
    }

    public SetupEntity toEntity(Setup domain) {
        if (domain == null) return null;
        return SetupEntity.builder()
                .setupId(domain.getSetupId())
                .cuotasPermitidas(domain.getCuotasPermitidas())
                .cuentaHonorariosPagar(domain.getCuentaHonorariosPagar())
                .build();
    }
}
