package um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.dto.ClaseChequeraRequest;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.dto.ClaseChequeraResponse;

@Component
public class ClaseChequeraDtoMapper {

    public ClaseChequera toDomain(ClaseChequeraRequest request) {
        if (request == null) return null;
        return ClaseChequera.builder()
                .claseChequeraId(request.getClaseChequeraId())
                .nombre(request.getNombre())
                .preuniversitario(request.getPreuniversitario())
                .grado(request.getGrado())
                .posgrado(request.getPosgrado())
                .curso(request.getCurso())
                .secundario(request.getSecundario())
                .titulo(request.getTitulo())
                .build();
    }

    public ClaseChequeraResponse toResponse(ClaseChequera domain) {
        if (domain == null) return null;
        return ClaseChequeraResponse.builder()
                .claseChequeraId(domain.getClaseChequeraId())
                .nombre(domain.getNombre())
                .preuniversitario(domain.getPreuniversitario())
                .grado(domain.getGrado())
                .posgrado(domain.getPosgrado())
                .curso(domain.getCurso())
                .secundario(domain.getSecundario())
                .titulo(domain.getTitulo())
                .build();
    }

}
