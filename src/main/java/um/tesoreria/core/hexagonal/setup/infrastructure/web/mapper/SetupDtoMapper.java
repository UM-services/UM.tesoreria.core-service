package um.tesoreria.core.hexagonal.setup.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.setup.domain.model.Setup;
import um.tesoreria.core.hexagonal.setup.infrastructure.web.dto.SetupResponse;

@Component
public class SetupDtoMapper {

    public SetupResponse toResponse(Setup domain) {
        if (domain == null) return null;
        return SetupResponse.builder()
                .setupId(domain.getSetupId())
                .cuotasPermitidas(domain.getCuotasPermitidas())
                .cuentaHonorariosPagar(domain.getCuentaHonorariosPagar())
                .build();
    }
}
