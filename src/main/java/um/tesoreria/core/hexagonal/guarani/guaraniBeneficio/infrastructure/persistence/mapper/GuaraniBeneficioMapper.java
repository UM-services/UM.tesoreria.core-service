package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.persistence.entity.GuaraniBeneficioEntity;

@Component
public class GuaraniBeneficioMapper {

    public GuaraniBeneficio toDomainModel(GuaraniBeneficioEntity entity) {
        if (entity == null) return null;
        return GuaraniBeneficio.builder()
                .guaraniBeneficioId(entity.getGuaraniBeneficioId())
                .requisito(entity.getRequisito())
                .porcentajeBeneficio(entity.getPorcentajeBeneficio())
                .build();
    }

    public GuaraniBeneficioEntity toEntity(GuaraniBeneficio domain) {
        if (domain == null) return null;
        GuaraniBeneficioEntity.GuaraniBeneficioEntityBuilder builder = GuaraniBeneficioEntity.builder()
                .guaraniBeneficioId(domain.getGuaraniBeneficioId())
                .requisito(domain.getRequisito());

        if (domain.getPorcentajeBeneficio() != null) {
            builder.porcentajeBeneficio(domain.getPorcentajeBeneficio());
        }

        return builder.build();
    }
}
