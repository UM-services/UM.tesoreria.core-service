package um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ContextoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.CrearBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ActualizarBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FuenteCompletado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.SolicitudEmision;

import java.math.BigDecimal;
import java.util.List;

/**
 * Puerto de entrada de la emisión individual de chequeras.
 */
public interface EmisionChequeraUseCase {

    ContextoEmision contexto(BigDecimal personaId, Integer documentoId, Integer lectivoId, Long usuarioId);

    BorradorChequera crearBorrador(CrearBorradorData data, String usuarioId);

    BorradorChequera obtenerBorrador(Long borradorId);

    List<BorradorChequera> borradoresPendientes(String usuarioId);

    BorradorChequera actualizarBorrador(Long borradorId, ActualizarBorradorData data);

    void descartarBorrador(Long borradorId);

    BorradorChequera completarBorrador(Long borradorId, FuenteCompletado fuente);

    BorradorChequera aplicarPoliticaArancelaria(Long borradorId, Integer productoId, Integer alternativaId, Integer cuotaId);

    ResultadoEmision emitir(Long borradorId, SolicitudEmision solicitud, Long usuarioId);
}
