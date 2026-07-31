package um.tesoreria.core.controller.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.kotlin.model.internal.ClickPagosEntity;
import um.tesoreria.core.service.facade.ClickPagosService;

@RestController
@RequestMapping("/clickPagos")
@RequiredArgsConstructor
public class ClickPagosController {

    private final ClickPagosService service;

    @PostMapping("/processLine/{verify}")
    public ResponseEntity<ClickPagosEntity> processLine(@RequestBody String line, @PathVariable Boolean verify) {
        return ResponseEntity.ok(service.processLine(line, verify));
    }
}
