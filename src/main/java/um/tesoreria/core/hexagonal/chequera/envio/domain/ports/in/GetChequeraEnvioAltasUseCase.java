package um.tesoreria.core.hexagonal.chequera.envio.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;

import java.util.List;

/**
 * Padrón de series a enviar, por lectivo, unidad académica y sede (paridad con «Revisar»
 * de {@code frmCheqMail}).
 */
public interface GetChequeraEnvioAltasUseCase {

    List<ChequeraEnvioAlta> getAltas(Integer lectivoId, Integer facultadId, Integer geograficaId);
}
