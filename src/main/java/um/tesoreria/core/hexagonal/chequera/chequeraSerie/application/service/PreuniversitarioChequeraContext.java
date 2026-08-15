package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service;

import um.tesoreria.core.kotlin.model.Build;

import java.math.BigDecimal;

record PreuniversitarioChequeraContext(
        int lectivoId,
        int tipoChequeraId,
        int facultadId,
        int geograficaId,
        BigDecimal personaId,
        int documentoId,
        Build build) {
}
