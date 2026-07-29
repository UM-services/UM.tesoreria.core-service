package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.out.GuaraniBeneficioRepository;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.persistence.entity.GuaraniBeneficioEntity;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.persistence.mapper.GuaraniBeneficioMapper;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.persistence.repository.JpaGuaraniBeneficioRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaGuaraniBeneficioRepositoryAdapter implements GuaraniBeneficioRepository {

    private final JpaGuaraniBeneficioRepository jpaGuaraniBeneficioRepository;
    private final GuaraniBeneficioMapper guaraniBeneficioMapper;

    @Override
    public Optional<GuaraniBeneficio> findByRequisito(Integer requisito) {
        return jpaGuaraniBeneficioRepository.findByRequisito(requisito)
                .map(guaraniBeneficioMapper::toDomainModel);
    }

    @Override
    public GuaraniBeneficio save(GuaraniBeneficio domain) {
        GuaraniBeneficioEntity entity = guaraniBeneficioMapper.toEntity(domain);
        return guaraniBeneficioMapper.toDomainModel(jpaGuaraniBeneficioRepository.save(entity));
    }
}
