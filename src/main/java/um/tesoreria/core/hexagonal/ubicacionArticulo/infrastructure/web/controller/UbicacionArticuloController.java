package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.core.JacksonException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloValidationException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.service.UbicacionArticuloService;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.dto.UbicacionArticuloRequest;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.dto.UbicacionArticuloResponse;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.mapper.UbicacionArticuloDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/ubicacionArticulo", "/api/tesoreria/core/ubicacionArticulo"})
@RequiredArgsConstructor
@Slf4j
public class UbicacionArticuloController {
    private final UbicacionArticuloService service;
    private final UbicacionArticuloDtoMapper mapper;

    @GetMapping("/")
    public ResponseEntity<List<UbicacionArticuloResponse>> findAll() {
        List<UbicacionArticuloResponse> responses = service.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vínculo del par"),
            @ApiResponse(responseCode = "404", description = "UBICACION_ARTICULO_NO_ENCONTRADO: el artículo no está asignado a esa ubicación", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))})
    @GetMapping("/{ubicacionId}/{articuloId}")
    public ResponseEntity<UbicacionArticuloResponse> findByUbicacionAndArticulo(
            @PathVariable Integer ubicacionId, @PathVariable Long articuloId) {
        return service.getByUbicacionAndArticulo(ubicacionId, articuloId)
                .map(ua -> ResponseEntity.ok(mapper.toResponse(ua)))
                .orElseThrow(() -> new UbicacionArticuloException(ubicacionId, articuloId));
    }

    @GetMapping("/articulo/{articuloId}")
    public ResponseEntity<List<UbicacionArticuloResponse>> findAllByArticuloId(@PathVariable Long articuloId) {
        List<UbicacionArticuloResponse> responses = service.findAllByArticuloId(articuloId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Asignar ubicación y cuenta a un artículo",
            description = "Idempotente por par (ubicacionId, articuloId): si el vínculo existe se le reemplaza la cuenta"
                    + " (nula la deja sin cuenta), si no se crea. Ubicación, artículo y cuenta (si viene) tienen que existir."
                    + " Registra alta o edición en el historial #404.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vínculo como quedó, con ubicación, artículo y cuenta"),
            @ApiResponse(responseCode = "400", description = "CAMPO_INVALIDO (con campo) o CUERPO_INVALIDO", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "CONFLICTO: otra operación tiene tomado el vínculo", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "415", description = "TIPO_DE_CONTENIDO_NO_SOPORTADO: falta application/json", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "ERROR_INTERNO (por ejemplo, falta la tabla gestion_escritura_historial)", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))})
    @PostMapping("/")
    public ResponseEntity<UbicacionArticuloResponse> save(@RequestBody UbicacionArticuloRequest request) {
        UbicacionArticulo domain = mapper.toDomain(request);
        UbicacionArticulo saved = service.save(domain);
        return ResponseEntity.ok(mapper.toResponse(saved));
    }

    // Errores como ProblemDetail: con server.error.include-message = never, un ResponseStatusException deja el cuerpo vacío

    @ExceptionHandler(UbicacionArticuloValidationException.class)
    public ProblemDetail campoInvalido(UbicacionArticuloValidationException ex, HttpServletRequest request) {
        return problema(HttpStatus.BAD_REQUEST, "CAMPO_INVALIDO", ex.getMessage(), ex.getCampo(), request);
    }

    @ExceptionHandler(UbicacionArticuloException.class)
    public ProblemDetail noEncontrado(UbicacionArticuloException ex, HttpServletRequest request) {
        return problema(HttpStatus.NOT_FOUND, "UBICACION_ARTICULO_NO_ENCONTRADO",
                "El artículo no está asignado a esa ubicación.", null, request);
    }

    @ExceptionHandler(UbicacionArticuloConflictException.class)
    public ProblemDetail conflicto(UbicacionArticuloConflictException ex, HttpServletRequest request) {
        var detail = ex.isReintentable()
                ? "Otra operación modificó la misma asignación al mismo tiempo. Consultá la asignación y reintentá si hace falta."
                : ex.isBloqueado()
                        ? "Otra operación tiene tomada la asignación. Reintentá en unos segundos."
                        : "La asignación choca con otro dato y no se guardó.";
        return problema(HttpStatus.CONFLICT, "CONFLICTO", detail, null, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail cuerpoInvalido(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return problema(HttpStatus.BAD_REQUEST, "CUERPO_INVALIDO",
                "El cuerpo no es JSON válido o tiene un valor que no entra en el tipo del campo.", campoDelCuerpo(ex), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail parametroInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return problema(HttpStatus.BAD_REQUEST, "PARAMETRO_INVALIDO",
                "El parámetro " + ex.getName() + " tiene un valor inválido.", ex.getName(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> errorInterno(Exception ex, HttpServletRequest request) throws Exception {
        if (ex instanceof HttpMediaTypeNotAcceptableException) {
            throw ex; // el cliente no acepta ningún formato en el que se pueda responder: lo resuelve Spring
        }
        if (ex instanceof ErrorResponse error) {
            // 415, parámetro faltante, etc.: estado y encabezados de Spring con el mismo contrato de error
            var problema = error.getBody();
            if (error.getStatusCode().value() == HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()) {
                problema.setDetail("El cuerpo tiene que enviarse como application/json.");
                problema.setProperty("codigo", "TIPO_DE_CONTENIDO_NO_SOPORTADO");
            } else if (error.getStatusCode().is5xxServerError()) {
                log.error("{} {}: error no controlado", request.getMethod(), request.getRequestURI(), ex);
                problema.setDetail("Error interno. El detalle quedó en el log del servicio.");
                problema.setProperty("codigo", "ERROR_INTERNO");
                return ResponseEntity.status(error.getStatusCode()).headers(error.getHeaders()).body(problema);
            } else {
                problema.setProperty("codigo", "SOLICITUD_INVALIDA");
            }
            log.info("{} {} -> {} {}: {}", request.getMethod(), request.getRequestURI(), error.getStatusCode().value(),
                    problema.getProperties().get("codigo"), problema.getDetail());
            return ResponseEntity.status(error.getStatusCode()).headers(error.getHeaders()).body(problema);
        }
        log.error("{} {}: error no controlado", request.getMethod(), request.getRequestURI(), ex);
        // Sin el mensaje de la excepción: puede traer SQL o nombres de restricción
        var detalle = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno. El detalle quedó en el log del servicio.");
        detalle.setProperty("codigo", "ERROR_INTERNO");
        return ResponseEntity.internalServerError().body(detalle);
    }

    private static ProblemDetail problema(HttpStatus status, String codigo, String detail, String campo, HttpServletRequest request) {
        log.info("{} {} -> {} {}{}: {}", request.getMethod(), request.getRequestURI(), status.value(), codigo,
                campo != null ? " campo=" + campo : "", detail);
        var problema = ProblemDetail.forStatusAndDetail(status, detail);
        problema.setProperty("codigo", codigo);
        if (campo != null) problema.setProperty("campo", campo);
        return problema;
    }

    /** Campo JSON donde falló la lectura (por ejemplo {@code ubicacionId: "x"}), si Jackson lo informa. */
    private static String campoDelCuerpo(HttpMessageNotReadableException ex) {
        for (Throwable t = ex.getCause(); t != null; t = t.getCause()) {
            if (t instanceof JacksonException je && !je.getPath().isEmpty()) {
                return je.getPath().getLast().getPropertyName();
            }
        }
        return null;
    }
}
