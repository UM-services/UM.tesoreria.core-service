package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.CanalGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudAnulacion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudReprogramacion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in.AnularEnvioDebitoUseCase;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in.GenerarArchivoUseCase;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in.PreviewGeneracionUseCase;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in.ReprogramarVencimientoDebitoUseCase;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto.CanalGeneracionResponse;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto.PreviewGeneracionResponse;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto.ResultadoOperacionDebitoResponse;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto.SolicitudAnulacionRequest;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto.SolicitudGeneracionRequest;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto.SolicitudReprogramacionRequest;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.mapper.GeneracionArchivoDtoMapper;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/tesoreria/core/cobranza/generacion")
@RequiredArgsConstructor
public class GeneracionArchivoController {

    private final PreviewGeneracionUseCase previewGeneracionUseCase;
    private final GenerarArchivoUseCase generarArchivoUseCase;
    private final AnularEnvioDebitoUseCase anularEnvioDebitoUseCase;
    private final ReprogramarVencimientoDebitoUseCase reprogramarVencimientoDebitoUseCase;
    private final GeneracionArchivoDtoMapper mapper;

    @GetMapping("/canal")
    public ResponseEntity<List<CanalGeneracionResponse>> canales() {
        return ResponseEntity.ok(Arrays.stream(CanalGeneracion.values()).map(mapper::toResponse).toList());
    }

    @PostMapping("/{canal}/preview")
    public ResponseEntity<PreviewGeneracionResponse> preview(@PathVariable String canal,
                                                             @RequestBody SolicitudGeneracionRequest request) {
        try {
            return ResponseEntity.ok(mapper.toResponse(
                    previewGeneracionUseCase.preview(solicitud(canal, request))));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @PostMapping("/{canal}")
    public ResponseEntity<byte[]> generar(@PathVariable String canal,
                                          @RequestBody SolicitudGeneracionRequest request) {
        try {
            ResultadoGeneracion resultado = generarArchivoUseCase.generar(solicitud(canal, request));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resultado.filename() + "\"")
                    .contentType(MediaType.parseMediaType(resultado.contentType()))
                    .body(resultado.bytes());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @PostMapping("/debito-directo/anulacion")
    public ResponseEntity<ResultadoOperacionDebitoResponse> anularDebitoDirecto(
            @RequestBody SolicitudAnulacionRequest request) {
        return anular(CanalGeneracion.DEBITO_DIRECTO, request);
    }

    @PostMapping("/debito-visa/anulacion")
    public ResponseEntity<ResultadoOperacionDebitoResponse> anularVisa(
            @RequestBody SolicitudAnulacionRequest request) {
        return anular(CanalGeneracion.DEBITO_VISA, request);
    }

    @PostMapping("/debito-directo/reprogramacion")
    public ResponseEntity<ResultadoOperacionDebitoResponse> reprogramar(
            @RequestBody SolicitudReprogramacionRequest request) {
        try {
            SolicitudReprogramacion solicitud = new SolicitudReprogramacion(
                    fecha(request.desde(), false), fecha(request.hasta(), true),
                    Boolean.TRUE.equals(request.soloSantander()),
                    Boolean.TRUE.equals(request.soloOtrosBancos()),
                    fecha(request.fechaVencimiento(), false));
            return ResponseEntity.ok(mapper.toResponse(
                    reprogramarVencimientoDebitoUseCase.reprogramar(solicitud)));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    private ResponseEntity<ResultadoOperacionDebitoResponse> anular(CanalGeneracion canal,
                                                                    SolicitudAnulacionRequest request) {
        try {
            SolicitudAnulacion solicitud = new SolicitudAnulacion(canal,
                    fecha(request.fechaEnvio(), false),
                    Boolean.TRUE.equals(request.soloSantander()),
                    Boolean.TRUE.equals(request.soloOtrosBancos()));
            return ResponseEntity.ok(mapper.toResponse(anularEnvioDebitoUseCase.anular(solicitud)));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    private SolicitudGeneracion solicitud(String canal, SolicitudGeneracionRequest request) {
        CanalGeneracion canalGeneracion = CanalGeneracion.fromPath(canal);
        return new SolicitudGeneracion(canalGeneracion,
                fecha(request.desde(), false), fecha(request.hasta(), true),
                request.grupo(),
                Boolean.TRUE.equals(request.soloSantander()),
                Boolean.TRUE.equals(request.soloOtrosBancos()),
                request.descuento());
    }

    private OffsetDateTime fecha(String value, boolean finDeDia) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value.trim());
        } catch (RuntimeException e) {
            try {
                LocalDate fecha = LocalDate.parse(value.trim());
                return (finDeDia ? fecha.atTime(LocalTime.MAX) : fecha.atStartOfDay())
                        .atOffset(ZoneOffset.UTC);
            } catch (RuntimeException e2) {
                throw new IllegalArgumentException("Fecha inválida: " + value);
            }
        }
    }

}
