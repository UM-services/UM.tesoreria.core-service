package um.tesoreria.core.hexagonal.chequeraSerie.infrastructure.web.mapper;

import um.tesoreria.core.hexagonal.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;

public class ChequeraSerieMapper {

    public static ChequeraSerie toHexagonal(ChequeraSerieEntity legacy) {
        if (legacy == null) {
            return null;
        }
        return ChequeraSerie.builder()
                .chequeraId(legacy.getChequeraId())
                .facultadId(legacy.getFacultadId())
                .tipoChequeraId(legacy.getTipoChequeraId())
                .chequeraSerieId(legacy.getChequeraSerieId())
                .personaId(legacy.getPersonaId())
                .documentoId(legacy.getDocumentoId())
                .alternativaId(legacy.getAlternativaId())
                .lectivoId(legacy.getLectivoId())
                .build();
    }
}
