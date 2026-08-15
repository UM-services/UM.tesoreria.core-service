package um.tesoreria.core.hexagonal.personas.legajo.infrastructure.web.mapper;

import org.springframework.stereotype.Component;

import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;
import um.tesoreria.core.hexagonal.personas.legajo.infrastructure.web.dto.LegajoRequest;
import um.tesoreria.core.hexagonal.personas.legajo.infrastructure.web.dto.LegajoResponse;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LegajoDtoMapper {

    public LegajoResponse toResponse(Legajo domain) {
        if (domain == null) return null;
        return LegajoResponse.builder()
                .legajoId(domain.getLegajoId())
                .personaId(domain.getPersonaId())
                .documentoId(domain.getDocumentoId())
                .facultadId(domain.getFacultadId())
                .numeroLegajo(domain.getNumeroLegajo())
                .fecha(domain.getFecha())
                .lectivoId(domain.getLectivoId())
                .planId(domain.getPlanId())
                .carreraId(domain.getCarreraId())
                .tieneCarrera(domain.getTieneCarrera())
                .geograficaId(domain.getGeograficaId())
                .contrasenha(domain.getContrasenha())
                .intercambio(domain.getIntercambio())
                .carrera(domain.getCarrera())
                .build();
    }

    public List<LegajoResponse> toResponse(List<Legajo> domains) {
        if (domains == null) return null;
        return domains.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Legajo toDomain(LegajoRequest request) {
        if (request == null) return null;
        Legajo.LegajoBuilder builder = Legajo.builder()
                .personaId(request.getPersonaId())
                .documentoId(request.getDocumentoId())
                .facultadId(request.getFacultadId())
                .fecha(request.getFecha())
                .lectivoId(request.getLectivoId())
                .planId(request.getPlanId())
                .carreraId(request.getCarreraId())
                .geograficaId(request.getGeograficaId())
                .contrasenha(request.getContrasenha());
        if (request.getNumeroLegajo() != null) builder.numeroLegajo(request.getNumeroLegajo());
        if (request.getTieneCarrera() != null) builder.tieneCarrera(request.getTieneCarrera());
        if (request.getIntercambio() != null) builder.intercambio(request.getIntercambio());
        return builder.build();
    }

    public List<Legajo> toDomain(List<LegajoRequest> requests) {
        if (requests == null) return null;
        return requests.stream().map(this::toDomain).collect(Collectors.toList());
    }
}