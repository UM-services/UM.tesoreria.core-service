package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.mapper.TipoChequeraMapper;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model.GuaraniPropuestaTipoChequera;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.persistence.entity.GuaraniPropuestaTipoChequeraEntity;

@Component
public class GuaraniPropuestaTipoChequeraMapper {
    private final TipoChequeraMapper tipoChequeraMapper;

    public GuaraniPropuestaTipoChequeraMapper(TipoChequeraMapper tipoChequeraMapper) {
        this.tipoChequeraMapper = tipoChequeraMapper;
    }

    public GuaraniPropuestaTipoChequera toDomainModel(GuaraniPropuestaTipoChequeraEntity entity) {
        if (entity == null) {
            return null;
        }
        return GuaraniPropuestaTipoChequera.builder()
                .guaraniPropuestaTipoChequeraId(entity.getGuaraniPropuestaTipoChequeraId())
                .propuestaGuarani(entity.getPropuestaGuarani())
                .lectivoId(entity.getLectivoId())
                .tipoChequeraId(entity.getTipoChequeraId())
                .tipoChequera(tipoChequeraMapper.toDomainModel(entity.getTipoChequera()))
                .build();
    }

    public GuaraniPropuestaTipoChequeraEntity toEntity(GuaraniPropuestaTipoChequera domain) {
        if (domain == null) {
            return null;
        }
        return GuaraniPropuestaTipoChequeraEntity.builder()
                .guaraniPropuestaTipoChequeraId(domain.getGuaraniPropuestaTipoChequeraId())
                .propuestaGuarani(domain.getPropuestaGuarani())
                .lectivoId(domain.getLectivoId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .build();
    }
}
