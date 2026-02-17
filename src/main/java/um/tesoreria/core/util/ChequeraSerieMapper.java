package um.tesoreria.core.util;

import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraSerie;

public class ChequeraSerieMapper {

    public static ChequeraSerie toHexagonal(um.tesoreria.core.kotlin.model.ChequeraSerie legacy) {
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
