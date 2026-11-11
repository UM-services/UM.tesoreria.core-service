package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.exception.ArchivoYaImportadoException;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.exception.ImportacionPagoException;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.service.ImportacionPagoService;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.CanalImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.SolicitudImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.EliminarImportacionUseCase;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.FindNoRegistradosUseCase;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.ImportarArchivoUseCase;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.ListarImportacionesUseCase;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.PreviewImportacionUseCase;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.ImportacionBusquedaRequest;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.ImportacionResumenResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.NoRegistradoResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.PreviewImportacionResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.RechazoResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.ResultadoEliminacionResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.ResultadoImportacionResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto.ResultadoReacreditacionResponse;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.mapper.ImportacionPagoDtoMapper;
import um.tesoreria.core.service.facade.PayPerTicFileService;
import um.tesoreria.core.util.transfer.FileInfo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/tesoreria/core/cobranza/importacion")
@RequiredArgsConstructor
public class ImportacionPagoController {

    private final PreviewImportacionUseCase previewImportacionUseCase;
    private final ImportarArchivoUseCase importarArchivoUseCase;
    private final EliminarImportacionUseCase eliminarImportacionUseCase;
    private final FindNoRegistradosUseCase findNoRegistradosUseCase;
    private final ListarImportacionesUseCase listarImportacionesUseCase;
    private final ImportacionPagoService importacionPagoService;
    private final ImportacionPagoDtoMapper mapper;
    private final PayPerTicFileService payPerTicFileService;

    @GetMapping("/canal")
    public ResponseEntity<List<String>> canales() {
        return ResponseEntity.ok(Arrays.stream(CanalImportacion.values()).map(Enum::name).toList());
    }

    @PostMapping(value = "/{canal}/preview", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PreviewImportacionResponse> preview(
            @PathVariable String canal,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "filename", required = false) String filename,
            @RequestPart(value = "desde", required = false) String desde,
            @RequestPart(value = "hasta", required = false) String hasta) {
        try {
            SolicitudImportacion solicitud = solicitud(canal, file, filename, desde, hasta, null, false);
            return ResponseEntity.ok(mapper.toResponse(previewImportacionUseCase.preview(solicitud)));
        } catch (ArchivoYaImportadoException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (ImportacionPagoException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No se pudo leer el archivo");
        }
    }

    @PostMapping(value = "/{canal}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultadoImportacionResponse> importar(
            @PathVariable String canal,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart(value = "filename", required = false) String filename,
            @RequestPart(value = "desde", required = false) String desde,
            @RequestPart(value = "hasta", required = false) String hasta,
            @RequestPart(value = "lineasAceptadas", required = false) String lineasAceptadas,
            @RequestPart(value = "permitirDuplicados", required = false) String permitirDuplicados) {
        try {
            SolicitudImportacion solicitud = solicitud(canal, file, filename, desde, hasta,
                    parseLineas(lineasAceptadas), Boolean.parseBoolean(permitirDuplicados));
            return ResponseEntity.ok(mapper.toResponse(importarArchivoUseCase.importar(solicitud)));
        } catch (ArchivoYaImportadoException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (ImportacionPagoException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No se pudo leer el archivo");
        }
    }

    /**
     * Sube el XLSX de PayPerTIC. El XLSX no es una rendición: llena {@code paypertic} y después se
     * importa por período con {@code /pay-per-tic/preview} y {@code /pay-per-tic}.
     */
    @PostMapping(value = "/pay-per-tic/archivo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> subirPayPerTic(@RequestPart("file") MultipartFile file) {
        try {
            payPerTicFileService.upload(new FileInfo(filename(file),
                    Base64.getEncoder().encodeToString(file.getBytes())));
            return ResponseEntity.noContent().build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No se pudo leer el archivo");
        }
    }

    @PostMapping(value = "/{canal}/{id}/reacreditar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResultadoReacreditacionResponse> reacreditar(
            @PathVariable String canal,
            @PathVariable Integer id,
            @RequestPart("file") MultipartFile file) {
        try {
            SolicitudImportacion solicitud = SolicitudImportacion.deArchivo(
                    CanalImportacion.fromPath(canal), filename(file), file.getBytes(), null, false);
            return ResponseEntity.ok(mapper.toResponse(
                    importacionPagoService.reacreditar(id, solicitud)));
        } catch (ImportacionPagoException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "No se pudo leer el archivo");
        }
    }

    @PostMapping("/buscar")
    public ResponseEntity<List<ImportacionResumenResponse>> buscar(@RequestBody ImportacionBusquedaRequest request) {
        try {
            return ResponseEntity.ok(listarImportacionesUseCase.listar(CanalImportacion.fromPath(request.canal()))
                    .stream().map(mapper::toResponse).toList());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImportacionResumenResponse> find(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(mapper.toResponse(importacionPagoService.findImportacion(id)));
        } catch (ImportacionPagoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/no-registrados")
    public ResponseEntity<List<NoRegistradoResponse>> noRegistrados(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(findNoRegistradosUseCase.noRegistrados(id)
                    .stream().map(mapper::toResponse).toList());
        } catch (ImportacionPagoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/{id}/rechazos")
    public ResponseEntity<List<RechazoResponse>> rechazos(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(importacionPagoService.rechazos(id)
                    .stream().map(mapper::toResponse).toList());
        } catch (ImportacionPagoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResultadoEliminacionResponse> eliminar(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(mapper.toResponse(eliminarImportacionUseCase.eliminar(id)));
        } catch (ImportacionPagoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    private SolicitudImportacion solicitud(String canal, MultipartFile file, String filenamePart,
                                           String desde, String hasta, List<Integer> lineasAceptadas,
                                           boolean permitirDuplicados) throws IOException {
        CanalImportacion canalImportacion = CanalImportacion.fromPath(canal);
        String nombre = filename(file, filenamePart);
        if (canalImportacion.isPorPeriodo()) {
            return SolicitudImportacion.dePeriodo(canalImportacion, nombre, fecha(desde, false),
                    fecha(hasta, true), lineasAceptadas, permitirDuplicados);
        }
        return SolicitudImportacion.deArchivo(canalImportacion, nombre, contenido(file),
                lineasAceptadas, permitirDuplicados);
    }

    private byte[] contenido(MultipartFile file) throws IOException {
        return file == null ? null : file.getBytes();
    }

    private String filename(MultipartFile file, String filenamePart) {
        if (file != null) {
            return filename(file);
        }
        if (filenamePart != null && !filenamePart.isBlank()) {
            return normalizar(filenamePart);
        }
        throw new ImportacionPagoException("Nombre de archivo requerido");
    }

    private String filename(MultipartFile file) {
        String nombre = file.getOriginalFilename();
        if (nombre == null || nombre.isBlank()) {
            throw new ImportacionPagoException("El archivo no tiene nombre");
        }
        return normalizar(nombre);
    }

    private String normalizar(String nombre) {
        String normalizado = nombre.replace('\\', '/');
        return normalizado.substring(normalizado.lastIndexOf('/') + 1);
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
                throw new ImportacionPagoException("Fecha inválida: " + value);
            }
        }
    }

    private List<Integer> parseLineas(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String limpio = value.replaceAll("[^0-9,]", "");
        if (limpio.isBlank()) {
            return null;
        }
        return Arrays.stream(limpio.split(","))
                .filter(parte -> !parte.isBlank())
                .map(Integer::valueOf)
                .toList();
    }

}
