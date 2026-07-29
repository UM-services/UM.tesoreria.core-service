package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.dto.GuaraniBeneficioRequest;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.dto.GuaraniBeneficioResponse;

@Component
public class GuaraniBeneficioDtoMapper {

    public GuaraniBeneficio toDomain(GuaraniBeneficioRequest request) {
        if (request == null) return null;
        GuaraniBeneficio.GuaraniBeneficioBuilder builder = GuaraniBeneficio.builder()
                .requisito(request.getRequisito());
        if (request.getPorcentajeBeneficio() != null) {
            builder.porcentajeBeneficio(request.getPorcentajeBeneficio());
        }
        return builder.build();
    }

    public GuaraniBeneficioResponse toResponse(GuaraniBeneficio domain) {
        if (domain == null) return null;
        return GuaraniBeneficioResponse.builder()
                .guaraniBeneficioId(domain.getGuaraniBeneficioId())
                .requisito(domain.getRequisito())
                .porcentajeBeneficio(domain.getPorcentajeBeneficio())
                .build();
    }
}
