package um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;

import java.util.List;

/**
 * Borradores ABIERTO de una facultad, lectivo y sede, enriquecidos para exportación.
 */
public interface ListarBorradoresSedeUseCase {

    List<BorradorChequera> listar(Integer facultadId, Integer lectivoId, Integer geograficaId);
}
