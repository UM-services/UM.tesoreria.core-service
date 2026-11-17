package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FuenteCompletado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.SolicitudEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.EmisionChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.ActualizarBorradorRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.BorradorResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.CompletarBorradorRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.ContextoEmisionResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.CrearBorradorRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.EmitirBorradorRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.ResultadoEmisionResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.mapper.EmisionDtoMapper;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping({"/api/tesoreria/core/emision/chequera", "/api/tesoreria/core/emisionChequera"})
@RequiredArgsConstructor
public class EmisionChequeraController {

    private final EmisionChequeraUseCase useCase;
    private final EmisionDtoMapper mapper;

    @GetMapping("/contexto/{personaId}/{documentoId}/{lectivoId}")
    public ResponseEntity<ContextoEmisionResponse> contexto(@PathVariable BigDecimal personaId,
                                                            @PathVariable Integer documentoId,
                                                            @PathVariable Integer lectivoId,
                                                            @RequestHeader(value = "X-User-Id", required = false) Long usuarioId) {
        return ResponseEntity.ok(mapper.toResponse(useCase.contexto(personaId, documentoId, lectivoId, usuarioId)));
    }

    @PostMapping("/borradores")
    @RequierePermiso("chequeras.emision.individual")
    public ResponseEntity<BorradorResponse> crear(@RequestBody CrearBorradorRequest request,
                                                  @RequestHeader(value = "X-User-Id", required = false) Long usuarioId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(useCase.crearBorrador(mapper.toData(request), usuario(usuarioId))));
    }

    @GetMapping("/borradores/{borradorId}")
    public ResponseEntity<BorradorResponse> obtener(@PathVariable Long borradorId) {
        return ResponseEntity.ok(mapper.toResponse(useCase.obtenerBorrador(borradorId)));
    }

    @GetMapping("/borradores/pendientes")
    public ResponseEntity<List<BorradorResponse>> pendientes(
            @RequestHeader(value = "X-User-Id", required = false) Long usuarioId) {
        return ResponseEntity.ok(useCase.borradoresPendientes(usuario(usuarioId)).stream()
                .map(mapper::toResponse).toList());
    }

    @PutMapping("/borradores/{borradorId}")
    @RequierePermiso("chequeras.emision.individual")
    public ResponseEntity<BorradorResponse> actualizar(@PathVariable Long borradorId,
                                                       @RequestBody ActualizarBorradorRequest request) {
        return ResponseEntity.ok(mapper.toResponse(useCase.actualizarBorrador(borradorId, mapper.toData(request))));
    }

    @DeleteMapping("/borradores/{borradorId}")
    @RequierePermiso("chequeras.emision.individual")
    public ResponseEntity<Void> descartar(@PathVariable Long borradorId) {
        useCase.descartarBorrador(borradorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/borradores/{borradorId}/completar")
    @RequierePermiso("chequeras.emision.individual")
    public ResponseEntity<BorradorResponse> completar(@PathVariable Long borradorId,
                                                      @RequestBody(required = false) CompletarBorradorRequest request) {
        FuenteCompletado fuente = request == null || request.fuente() == null ? FuenteCompletado.DISENO : request.fuente();
        return ResponseEntity.ok(mapper.toResponse(useCase.completarBorrador(borradorId, fuente)));
    }

    @PostMapping("/borradores/{borradorId}/politica/{productoId}/{alternativaId}/{cuotaId}")
    @RequierePermiso("chequeras.emision.individual")
    public ResponseEntity<BorradorResponse> politica(@PathVariable Long borradorId,
                                                     @PathVariable Integer productoId,
                                                     @PathVariable Integer alternativaId,
                                                     @PathVariable Integer cuotaId) {
        return ResponseEntity.ok(mapper.toResponse(
                useCase.aplicarPoliticaArancelaria(borradorId, productoId, alternativaId, cuotaId)));
    }

    @PostMapping("/borradores/{borradorId}/emision")
    @RequierePermiso("chequeras.emision.individual")
    public ResponseEntity<ResultadoEmisionResponse> emitir(@PathVariable Long borradorId,
                                                           @RequestBody EmitirBorradorRequest request,
                                                           @RequestHeader(value = "X-User-Id", required = false) Long usuarioId) {
        SolicitudEmision solicitud = SolicitudEmision.builder()
                .version(request.version())
                .forzarNueva(Boolean.TRUE.equals(request.forzarNueva()))
                .forzarDeuda(Boolean.TRUE.equals(request.forzarDeuda()))
                .motivoOverride(request.motivoOverride())
                .claveIdempotencia(request.claveIdempotencia())
                .build();
        return ResponseEntity.ok(mapper.toResponse(useCase.emitir(borradorId, solicitud, usuarioId)));
    }

    private static String usuario(Long usuarioId) {
        return usuarioId == null ? null : String.valueOf(usuarioId);
    }
}
