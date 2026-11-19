package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ActualizarBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ContextoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.CrearBorradorData;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FuenteCompletado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.SolicitudEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.EmisionChequeraUseCase;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmisionChequeraFacade implements EmisionChequeraUseCase {

    private final BorradorEmisionService borradorEmisionService;
    private final EmisionChequeraService emisionChequeraService;

    @Override
    public ContextoEmision contexto(BigDecimal personaId, Integer documentoId, Integer lectivoId, Long usuarioId) {
        return borradorEmisionService.contexto(personaId, documentoId, lectivoId, usuarioId);
    }

    @Override
    public BorradorChequera crearBorrador(CrearBorradorData data, String usuarioId) {
        return borradorEmisionService.crearBorrador(data, usuarioId);
    }

    @Override
    public BorradorChequera obtenerBorrador(Long borradorId) {
        return borradorEmisionService.obtenerBorrador(borradorId);
    }

    @Override
    public List<BorradorChequera> borradoresPendientes(String usuarioId) {
        return borradorEmisionService.borradoresPendientes(usuarioId);
    }

    @Override
    public BorradorChequera actualizarBorrador(Long borradorId, ActualizarBorradorData data) {
        return borradorEmisionService.actualizarBorrador(borradorId, data);
    }

    @Override
    public void descartarBorrador(Long borradorId) {
        borradorEmisionService.descartarBorrador(borradorId);
    }

    @Override
    public BorradorChequera completarBorrador(Long borradorId, FuenteCompletado fuente) {
        return borradorEmisionService.completarBorrador(borradorId, fuente);
    }

    @Override
    public BorradorChequera aplicarPoliticaArancelaria(Long borradorId, Integer productoId, Integer alternativaId,
                                                        Integer cuotaId) {
        return borradorEmisionService.aplicarPoliticaArancelaria(borradorId, productoId, alternativaId, cuotaId);
    }

    @Override
    public ResultadoEmision emitir(Long borradorId, SolicitudEmision solicitud, Long usuarioId) {
        return emisionChequeraService.emitir(borradorId, solicitud, usuarioId);
    }
}
