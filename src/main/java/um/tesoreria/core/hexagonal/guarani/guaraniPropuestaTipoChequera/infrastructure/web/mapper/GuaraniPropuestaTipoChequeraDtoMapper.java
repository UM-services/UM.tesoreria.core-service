package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.web.mapper.TipoChequeraDtoMapper;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model.GuaraniPropuestaTipoChequera;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.web.dto.GuaraniPropuestaTipoChequeraRequest;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.web.dto.GuaraniPropuestaTipoChequeraResponse;

@Component
public class GuaraniPropuestaTipoChequeraDtoMapper {
    private final TipoChequeraDtoMapper tipoChequeraDtoMapper;

    public GuaraniPropuestaTipoChequeraDtoMapper(TipoChequeraDtoMapper tipoChequeraDtoMapper) {
        this.tipoChequeraDtoMapper = tipoChequeraDtoMapper;
    }

    public GuaraniPropuestaTipoChequera toDomain(GuaraniPropuestaTipoChequeraRequest request) {
        if (request == null) {
            return null;
        }
        return GuaraniPropuestaTipoChequera.builder()
                .propuestaGuarani(request.getPropuestaGuarani())
                .lectivoId(request.getLectivoId())
                .tipoChequeraId(request.getTipoChequeraId())
                .build();
    }

    public GuaraniPropuestaTipoChequeraResponse toResponse(GuaraniPropuestaTipoChequera domain) {
        if (domain == null) {
            return null;
        }
        return GuaraniPropuestaTipoChequeraResponse.builder()
                .guaraniPropuestaTipoChequeraId(domain.getGuaraniPropuestaTipoChequeraId())
                .propuestaGuarani(domain.getPropuestaGuarani())
                .lectivoId(domain.getLectivoId())
                .tipoChequeraId(domain.getTipoChequeraId())
                .tipoChequera(tipoChequeraDtoMapper.toResponse(domain.getTipoChequera()))
                .build();
    }
}
