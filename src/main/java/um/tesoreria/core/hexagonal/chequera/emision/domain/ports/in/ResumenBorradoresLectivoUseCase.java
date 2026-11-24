package um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.FacultadSedeBorradorResponse;

import java.util.List;

/**
 * Resumen de borradores ABIERTO por facultad y sede para un lectivo.
 */
public interface ResumenBorradoresLectivoUseCase {

    List<FacultadSedeBorradorResponse> resumenLectivo(Integer lectivoId);
}
