package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.GeneracionBorradoresUseCase;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.GeneracionEmisionRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.GeneracionFiltroRequest;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.GeneracionJobDetalleResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.GeneracionJobResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.GeneracionPadronResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.mapper.GeneracionDtoMapper;

import java.util.List;

/**
 * Generación masiva de borradores sobre las chequeras de una facultad y sede y emisión previa al
 * envío masivo.
 *
 * <p>Dos etapas asíncronas: {@code BORRADORES} crea/reutiliza un borrador por serie (sin tocar
 * {@code chequera_*}) y {@code EMISION} los emite. El front consulta el avance por HTTP.</p>
 */
@RestController
@RequestMapping({"/emision/chequera/generacion", "/api/tesoreria/core/emision/chequera/generacion"})
@RequiredArgsConstructor
public class GeneracionBorradoresController {

    private final GeneracionBorradoresUseCase useCase;
    private final GeneracionDtoMapper mapper;

    /** Vista previa del padrón de series que se van a procesar. */
    @PostMapping("/preview")
    @RequierePermiso("chequeras.generacion.borradores")
    public ResponseEntity<List<GeneracionPadronResponse>> preview(@RequestBody GeneracionFiltroRequest request) {
        return ResponseEntity.ok(useCase.preview(mapper.toFiltro(request)).stream()
                .map(mapper::toResponse).toList());
    }

    /** Etapa A: crea o reutiliza un borrador por cada serie del padrón. */
    @PostMapping("/borradores")
    @RequierePermiso("chequeras.generacion.borradores")
    public ResponseEntity<GeneracionJobResponse> iniciarBorradores(@RequestBody GeneracionFiltroRequest request,
                                                                    HttpServletRequest http) {
        var job = useCase.iniciarBorradores(mapper.toFiltro(request), usuarioId(http));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(mapper.toResponse(job));
    }

    /** Etapa B desde el job de borradores: emite todos o los indicados. */
    @PostMapping("/job/{jobId}/emision")
    @RequierePermiso("chequeras.generacion.emision")
    public ResponseEntity<GeneracionJobResponse> iniciarEmision(@PathVariable Long jobId,
                                                                @RequestBody(required = false) GeneracionEmisionRequest request,
                                                                HttpServletRequest http) {
        List<Long> borradorIds = request == null ? null : request.borradorIds();
        boolean forzarDeuda = request != null && Boolean.TRUE.equals(request.forzarDeuda());
        String motivoOverride = request == null ? null : request.motivoOverride();
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(mapper.toResponse(useCase.iniciarEmision(jobId, borradorIds, forzarDeuda, motivoOverride,
                        usuarioId(http))));
    }

    /**
     * Etapa B directa, sin job de origen: emite los borradores abiertos del filtro. La usa la
     * pantalla de envío masivo como generación previa.
     */
    @PostMapping("/emision")
    @RequierePermiso("chequeras.generacion.emision")
    public ResponseEntity<GeneracionJobResponse> iniciarEmisionDirecta(@RequestBody GeneracionFiltroRequest request,
                                                                       HttpServletRequest http) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(mapper.toResponse(useCase.iniciarEmisionDirecta(mapper.toFiltro(request), usuarioId(http))));
    }

    @GetMapping("/job/{jobId}")
    @RequierePermiso("chequeras.generacion.borradores")
    public ResponseEntity<GeneracionJobDetalleResponse> consultar(@PathVariable Long jobId) {
        return ResponseEntity.ok(mapper.toResponse(useCase.consultar(jobId)));
    }

    @PostMapping("/job/{jobId}/cancelar")
    @RequierePermiso("chequeras.generacion.borradores")
    public ResponseEntity<GeneracionJobResponse> cancelar(@PathVariable Long jobId) {
        return ResponseEntity.ok(mapper.toResponse(useCase.cancelar(jobId)));
    }

    @GetMapping("/job/pendientes")
    @RequierePermiso("chequeras.generacion.borradores")
    public ResponseEntity<List<GeneracionJobResponse>> pendientes() {
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
