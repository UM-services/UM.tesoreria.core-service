package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.model.ChequeraSerieControl;
import um.tesoreria.core.util.Tool;

import java.math.BigDecimal;

/** Centralizes the business defaults used only by pre-university chequeras. */
@Component
final class PreuniversitarioChequeraPolicy {

    private static final int ARANCEL_TIPO_ID = 1;
    private static final int CURSO_ID = 7;
    private static final int TIPO_IMPRESION_ID = 2;
    private static final int FACULTAD_ALTERNATIVA_PRINCIPAL = 14;
    private static final int ALTERNATIVA_PRINCIPAL = 1;
    private static final int ALTERNATIVA_GENERAL = 2;
    private static final String OBSERVACIONES = "Generated From Guaraní";
    private static final String USUARIO = "guarani";

    ChequeraSerie createSerie(PreuniversitarioChequeraContext context,
                               ChequeraSerieControl control, BigDecimal becaPorcentaje) {

        return ChequeraSerie.builder()
                .facultadId(control.getFacultadId())
                .tipoChequeraId(control.getTipoChequeraId())
                .chequeraSerieId(control.getChequeraSerieId())
                .personaId(context.personaId())
                .documentoId(context.documentoId())
                .lectivoId(context.lectivoId())
                .arancelTipoId(ARANCEL_TIPO_ID)
                .cursoId(CURSO_ID)
                .asentado((byte) 0)
                .geograficaId(context.geograficaId())
                .fecha(Tool.dateAbsoluteArgentina())
                .cuotasPagadas(0)
                .observaciones(OBSERVACIONES)
                .alternativaId(alternativeFor(context.facultadId()))
                .algoPagado((byte) 0)
                .tipoImpresionId(TIPO_IMPRESION_ID)
                .flagPayperTic((byte) 0)
                .usuarioId(USUARIO)
                .enviado((byte) 0)
                .retenida((byte) 0)
                .version(context.build().getBuild())
                .hpum((byte) 0)
                .becaPorcentaje(becaPorcentaje)
                .build();
    }

    private int alternativeFor(int facultadId) {
        return facultadId == FACULTAD_ALTERNATIVA_PRINCIPAL
                ? ALTERNATIVA_PRINCIPAL
                : ALTERNATIVA_GENERAL;
    }
}
