package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.configuration.security.RequierePermisoInterceptor;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.IncompletasChequerasUseCase;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.IncompletasEmisionRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.IncompletasJobDetalleResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.IncompletasJobRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.IncompletasJobResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.mapper.IncompletasDtoMapper;

import java.util.List;

/**
 * Reparación masiva de chequeras incompletas ({@code frmIncompletas}).
 *
 * <p>Dos etapas: primero se generan/reutilizan borradores (no toca {@code chequera_*}) y luego se
 * emiten. Ambas son jobs asíncronos con avance consultable.</p>
 */
@RestController
@RequestMapping({"/emision/chequera/incompletas", "/api/tesoreria/core/emision/chequera/incompletas"})
@RequiredArgsConstructor
@Slf4j
public class IncompletasChequeraController {

    private final IncompletasChequerasUseCase useCase;
    private final IncompletasDtoMapper mapper;

    /** Etapa A: genera los borradores que completan las cuotas faltantes. */
    @PostMapping("/borradores")
    @RequierePermiso("chequeras.incompletas.generar")
    public ResponseEntity<IncompletasJobResponse> iniciarBorradores(@RequestBody IncompletasJobRequest request,
                                                                    HttpServletRequest http) {
        var job = useCase.iniciarBorradores(mapper.toFiltro(request), usuarioId(http));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(mapper.toResponse(job));
    }

    /** Etapa B: emite los borradores del job (todos o los indicados). */
    @PostMapping("/job/{jobId}/emision")
    @RequierePermiso("chequeras.incompletas.generar")
    public ResponseEntity<IncompletasJobResponse> iniciarEmision(@PathVariable Long jobId,
                                                                 @RequestBody(required = false) IncompletasEmisionRequest request,
                                                                 HttpServletRequest http) {
        var borradorIds = request == null ? null : request.borradorIds();
        var job = useCase.iniciarEmision(jobId, borradorIds, usuarioId(http));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(mapper.toResponse(job));
    }

    @GetMapping("/job/{jobId}")
    @RequierePermiso("chequeras.incompletas.consultar")
    public ResponseEntity<IncompletasJobDetalleResponse> consultar(@PathVariable Long jobId) {
        return ResponseEntity.ok(mapper.toResponse(useCase.consultar(jobId)));
    }

    @PostMapping("/job/{jobId}/cancelar")
    @RequierePermiso("chequeras.incompletas.generar")
    public ResponseEntity<IncompletasJobResponse> cancelar(@PathVariable Long jobId) {
        return ResponseEntity.ok(mapper.toResponse(useCase.cancelar(jobId)));
    }

    @GetMapping("/job/pendientes")
    @RequierePermiso("chequeras.incompletas.consultar")
    public ResponseEntity<List<IncompletasJobResponse>> pendientes() {
        return ResponseEntity.ok(useCase.pendientes().stream().map(mapper::toResponse).toList());
    }

    private Long usuarioId(HttpServletRequest request) {
        String header = request.getHeader(RequierePermisoInterceptor.USER_ID_HEADER);
        if (header == null || header.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(header.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
