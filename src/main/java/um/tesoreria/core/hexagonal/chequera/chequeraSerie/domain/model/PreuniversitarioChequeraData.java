package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model;

import java.math.BigDecimal;

public record PreuniversitarioChequeraData(
        Integer propuesta,
        Integer responsableAcademica,
        Integer ubicacion,
        BigDecimal personaId,
        Integer documentoId) {
}
