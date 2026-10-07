package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.web.controller;

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
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.application.service.ArticuloService;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.web.dto.ArticuloRequest;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.web.dto.ArticuloSearchResponse;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.web.dto.ArticuloResponse;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.web.mapper.ArticuloDtoMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import um.tesoreria.core.model.PaginatedResponse;


@RestController
@RequestMapping({"/articulo", "/api/tesoreria/core/articulo"})
@RequiredArgsConstructor
@Slf4j
public class ArticuloController {

    private final ArticuloService articuloService;
    private final ArticuloDtoMapper articuloDtoMapper;
    private final CuentaService cuentaService;
    

    @PostMapping("/")
    public ResponseEntity<ArticuloResponse> createArticulo(@RequestBody ArticuloRequest articuloRequest) {
        Articulo articulo = articuloDtoMapper.toDomain(articuloRequest);
        Articulo createdArticulo = articuloService.createArticulo(articulo);
        return new ResponseEntity<>(articuloDtoMapper.toResponse(createdArticulo), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticuloResponse> getArticuloById(@PathVariable Long id) {
        return articuloService.getArticuloById(id)
                .map(domain -> {
                    ArticuloResponse dto = articuloDtoMapper.toResponse(domain);
                    if (dto.getNumeroCuenta() != null) {
                        cuentaService.findByNumeroCuenta(dto.getNumeroCuenta())
                                .ifPresent(dto::setCuenta);
                    }
                    return ResponseEntity.ok(dto);
                })
                .orElseThrow(() -> new ArticuloException(id));
    }

    @PostMapping("/search")
    public ResponseEntity<List<ArticuloSearchResponse>> findByStrings(@RequestBody List<String> conditions) {
        List<ArticuloSearchResponse> responses = articuloService.searchArticulos(conditions).stream()
                .map(domain -> {
                    ArticuloSearchResponse dto = articuloDtoMapper.toSearchResponse(domain);
                    if (dto.getNumeroCuenta() != null) {
                        cuentaService.findByNumeroCuenta(dto.getNumeroCuenta())
                                .ifPresent(dto::setCuenta);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/")
    public ResponseEntity<List<ArticuloResponse>> getAllArticulos() {
        List<ArticuloResponse> responses = articuloService.getAllArticulos().stream()
                .map(articuloDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/tipo/{tipo}/page")
    public ResponseEntity<PaginatedResponse<ArticuloResponse>> getPaginatedByTipo(
            @PathVariable String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
            
        PaginatedResponse<Articulo> domainPage = articuloService.getPaginatedArticulosByTipo(tipo, page, size);
        
        List<ArticuloResponse> responseList = domainPage.getData().stream()
                .map(domain -> {
                    ArticuloResponse dto = articuloDtoMapper.toResponse(domain);
                    if (dto.getNumeroCuenta() != null) {
                        cuentaService.findByNumeroCuenta(dto.getNumeroCuenta())
                                .ifPresent(dto::setCuenta);
                    }
                    return dto;
                })
                .collect(Collectors.toList());
                
        PaginatedResponse<ArticuloResponse> paginatedResponse = new PaginatedResponse<>(
                responseList,
                domainPage.getTotalElements(),
                domainPage.getTotalPages(),
                domainPage.getCurrentPage(),
                domainPage.getPageSize()
        );
        
        return ResponseEntity.ok(paginatedResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticuloResponse> updateArticulo(@PathVariable Long id, @RequestBody ArticuloRequest articuloRequest) {
        Articulo cambios = articuloDtoMapper.toDomain(articuloRequest);
        return ResponseEntity.ok(articuloDtoMapper.toResponse(articuloService.updateArticulo(id, cambios)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticulo(@PathVariable Long id) {
        articuloService.deleteArticulo(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/new")
    public ResponseEntity<ArticuloResponse> getNewArticulo() {
        return ResponseEntity.ok(articuloDtoMapper.toResponse(articuloService.getNewArticulo()));
    }

    // Errores como ProblemDetail: con server.error.include-message = never, un ResponseStatusException deja el cuerpo vacío

    @ExceptionHandler(ArticuloValidationException.class)
    public ProblemDetail campoInvalido(ArticuloValidationException ex, HttpServletRequest request) {
        return problema(HttpStatus.BAD_REQUEST, "CAMPO_INVALIDO", ex.getMessage(), ex.getCampo(), request);
    }

    @ExceptionHandler(ArticuloException.class)
    public ProblemDetail noEncontrado(ArticuloException ex, HttpServletRequest request) {
        return problema(HttpStatus.NOT_FOUND, "ARTICULO_NO_ENCONTRADO", "El artículo no existe.", null, request);
    }

    @ExceptionHandler(ArticuloConflictException.class)
    public ProblemDetail conflicto(ArticuloConflictException ex, HttpServletRequest request) {
        var id = ex.getArticuloId();
        return switch (ex.getMotivo()) {
            case ID_DUPLICADO -> problema(HttpStatus.CONFLICT, "ARTICULO_ID_DUPLICADO",
                    "El id ya existe. Si no estás seguro de que tu alta anterior se haya guardado, consultá GET /articulo/"
                            + id + " antes de crear con otro id.", "articuloId", request);
            case REFERENCIADO -> {
                var detalle = problema(HttpStatus.CONFLICT, "ARTICULO_REFERENCIADO",
                        "El artículo " + id + " está referenciado y no se puede borrar. Este servicio guarda habilitado"
                                + " pero no filtra por ese campo: si se usa habilitado = 0 como baja, la web y VB6 tienen que respetarlo.",
                        null, request);
                detalle.setProperty("referencias", ex.getReferencias().stream().map(r -> {
                    Map<String, Object> referencia = new LinkedHashMap<>();
                    referencia.put("tabla", r.tabla());
                    if (r.cantidad() != null) referencia.put("cantidad", r.cantidad());
                    return referencia;
                }).toList());
                yield detalle;
            }
            case CONFLICTO -> problema(HttpStatus.CONFLICT, "CONFLICTO",
                    "La escritura del artículo " + id + " choca con otro dato. Releé el artículo y reintentá.", null, request);
            case BLOQUEADO -> problema(HttpStatus.CONFLICT, "CONFLICTO",
                    "Otra operación tiene tomado el artículo " + id + ". Reintentá en unos segundos.", null, request);
        };
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

    /** Campo JSON donde falló la lectura (por ejemplo {@code habilitado: 300}), si Jackson lo informa. */
    private static String campoDelCuerpo(HttpMessageNotReadableException ex) {
        for (Throwable t = ex.getCause(); t != null; t = t.getCause()) {
            if (t instanceof JacksonException je && !je.getPath().isEmpty()) {
                return je.getPath().getLast().getPropertyName();
            }
        }
        return null;
    }
}
