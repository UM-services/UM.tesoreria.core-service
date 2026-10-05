package um.tesoreria.core.hexagonal.umhub.consulta.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.umhub.consulta.application.service.ConsultaPersonaDeudaService;
import um.tesoreria.core.hexagonal.umhub.consulta.infrastructure.web.dto.ConsultaDeudaResponse;
import um.tesoreria.core.hexagonal.umhub.consulta.infrastructure.web.dto.ConsultaPersonaResponse;
import um.tesoreria.core.hexagonal.umhub.consulta.infrastructure.web.mapper.ConsultaDtoMapper;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Endpoints de solo lectura destinados al hub externo (tesoreria-umhub-service).
 * Consulta por numero de documento SIN tipo: el agregado de tipos corre por cuenta
 * de la slice (ver GetConsultaPersonaUseCaseImpl). DTOs filtrados: nunca password/cbu/cuit.
 */
@RestController
@RequestMapping("/api/tesoreria/core/umhub/consulta")
@RequiredArgsConstructor
@Slf4j
public class ConsultaPersonaDeudaController {

    private static final Pattern NUMERO_DOCUMENTO = Pattern.compile("\\d{6,10}");

    private final ConsultaPersonaDeudaService service;
    private final ConsultaDtoMapper dtoMapper;

    @Operation(summary = "Datos personales + domicilio/contacto por numero de documento (sin tipo)")
    @GetMapping("/persona/{numeroDocumento}")
    public ResponseEntity<ConsultaPersonaResponse> getPersona(@PathVariable String numeroDocumento) {
        ConsultaPersonaResponse response = service.findPersona(parseNumero(numeroDocumento))
                .map(dtoMapper::toPersonaResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Detalle de deuda agregado por numero de documento (todos los tipos del mismo titular)")
    @GetMapping("/persona/{numeroDocumento}/deuda")
    public ResponseEntity<ConsultaDeudaResponse> getDeuda(@PathVariable String numeroDocumento,
            @RequestParam(name = "extended", defaultValue = "false") boolean extended) {
        ConsultaDeudaResponse response = service.findDeuda(parseNumero(numeroDocumento), extended)
                .map(dtoMapper::toDeudaResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));
        return ResponseEntity.ok(response);
    }

    private BigDecimal parseNumero(String numeroDocumento) {
        if (numeroDocumento == null || !NUMERO_DOCUMENTO.matcher(numeroDocumento).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "numeroDocumento invalido: se requieren de 6 a 10 digitos");
        }
        return new BigDecimal(numeroDocumento);
    }
}
