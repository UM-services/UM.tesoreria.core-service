package um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.mapper.ClaseChequeraDtoMapper;
import um.tesoreria.core.hexagonal.geografica.infrastructure.web.mapper.GeograficaDtoMapper;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.web.dto.TipoChequeraRequest;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.web.dto.TipoChequeraResponse;

@Component
public class TipoChequeraDtoMapper {

    private final GeograficaDtoMapper geograficaDtoMapper;
    private final ClaseChequeraDtoMapper claseChequeraDtoMapper;

    public TipoChequeraDtoMapper(GeograficaDtoMapper geograficaDtoMapper, ClaseChequeraDtoMapper claseChequeraDtoMapper) {
        this.geograficaDtoMapper = geograficaDtoMapper;
        this.claseChequeraDtoMapper = claseChequeraDtoMapper;
    }

    public TipoChequera toDomain(TipoChequeraRequest request) {
        if (request == null) return null;
        return TipoChequera.builder()
                .tipoChequeraId(request.getTipoChequeraId())
                .nombre(request.getNombre())
                .prefijo(request.getPrefijo())
                .geograficaId(request.getGeograficaId())
                .claseChequeraId(request.getClaseChequeraId())
                .imprimir(request.getImprimir())
                .contado(request.getContado())
                .multiple(request.getMultiple())
                .emailCopia(request.getEmailCopia())
                .build();
    }

    public TipoChequeraResponse toResponse(TipoChequera domain) {
        if (domain == null) return null;
        return TipoChequeraResponse.builder()
                .tipoChequeraId(domain.getTipoChequeraId())
                .nombre(domain.getNombre())
                .prefijo(domain.getPrefijo())
                .geograficaId(domain.getGeograficaId())
                .claseChequeraId(domain.getClaseChequeraId())
                .imprimir(domain.getImprimir())
                .contado(domain.getContado())
                .multiple(domain.getMultiple())
                .emailCopia(domain.getEmailCopia())
                .geografica(geograficaDtoMapper.toResponse(domain.getGeografica()))
                .claseChequera(claseChequeraDtoMapper.toResponse(domain.getClaseChequera()))
                .build();
    }
}
