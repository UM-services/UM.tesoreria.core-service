package um.tesoreria.core.hexagonal.personas.legajo.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;

import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;
import um.tesoreria.core.hexagonal.personas.legajo.infrastructure.persistence.entity.LegajoEntity;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LegajoMapper {

    public Legajo toDomain(LegajoEntity entity) {
        if (entity == null) return null;
        return Legajo.builder()
                .legajoId(entity.getLegajoId())
                .personaId(entity.getPersonaId())
                .documentoId(entity.getDocumentoId())
                .facultadId(entity.getFacultadId())
                .numeroLegajo(entity.getNumeroLegajo())
                .fecha(entity.getFecha())
                .lectivoId(entity.getLectivoId())
                .planId(entity.getPlanId())
                .carreraId(entity.getCarreraId())
                .tieneCarrera(entity.getTieneCarrera())
                .geograficaId(entity.getGeograficaId())
                .contrasenha(entity.getContrasenha())
                .intercambio(entity.getIntercambio())
                .carrera(entity.getCarrera())
                .build();
    }

    public List<Legajo> toDomain(List<LegajoEntity> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    public LegajoEntity toEntity(Legajo domain) {
        if (domain == null) return null;
        return LegajoEntity.builder()
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

    public List<LegajoEntity> toEntity(List<Legajo> domains) {
        if (domains == null) return null;
        return domains.stream().map(this::toEntity).collect(Collectors.toList());
    }
}