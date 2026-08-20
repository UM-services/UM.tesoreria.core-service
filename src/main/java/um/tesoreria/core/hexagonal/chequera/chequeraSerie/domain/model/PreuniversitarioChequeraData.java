package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model;

import java.math.BigDecimal;
import java.util.List;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.RequisitoPresentadoGuarani;

public record PreuniversitarioChequeraData(
        Integer propuesta,
        Integer responsableAcademica,
        Integer ubicacion,
        BigDecimal personaId,
        Integer documentoId,
        List<RequisitoPresentadoGuarani> requisitosPresentados) {

    public PreuniversitarioChequeraData(Integer propuesta, Integer responsableAcademica, Integer ubicacion,
                                        BigDecimal personaId, Integer documentoId) {
        this(propuesta, responsableAcademica, ubicacion, personaId, documentoId, List.of());
    }
}
