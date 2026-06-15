package um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.exception.MercadoPagoContextException;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.service.MercadoPagoContextService;
import um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.web.dto.MercadoPagoContextResponse;
import um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.web.mapper.MercadoPagoContextDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/core/mercadoPagoContext")
@RequiredArgsConstructor
public class MercadoPagoContextController {

    private final MercadoPagoContextService service;
    private final MercadoPagoContextDtoMapper dtoMapper;

    @GetMapping("/cuota/activo/{chequeraCuotaId}")
    public ResponseEntity<MercadoPagoContextResponse> findActivoByChequeraCuotaId(@PathVariable Long chequeraCuotaId) {
        try {
            var domain = service.findActiveByChequeraCuotaId(chequeraCuotaId);
            return ResponseEntity.ok(dtoMapper.toResponse(domain));
        } catch (MercadoPagoContextException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{mercadoPagoContextId}")
    public ResponseEntity<MercadoPagoContextResponse> findByMercadoPagoContextId(@PathVariable Long mercadoPagoContextId) {
        try {
            var domain = service.findByMercadoPagoContextId(mercadoPagoContextId);
            return ResponseEntity.ok(dtoMapper.toResponse(domain));
        } catch (MercadoPagoContextException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/all/active/cuota/id")
    public ResponseEntity<List<Long>> findAllActiveChequeraCuota() {
        return ResponseEntity.ok(service.findAllActiveChequeraCuota());
    }

    @GetMapping("/all/active/to/change")
    public ResponseEntity<List<Long>> findAllActiveToChange() {
        return ResponseEntity.ok(service.findAllActiveToChange());
    }

    @GetMapping("/pagos/sin/imputar")
    public ResponseEntity<List<MercadoPagoContextResponse>> findAllSinImputar() {
        List<MercadoPagoContextResponse> responses = service.findAllSinImputar().stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

}
