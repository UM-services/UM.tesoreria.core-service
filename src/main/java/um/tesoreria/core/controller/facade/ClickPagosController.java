package um.tesoreria.core.controller.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.kotlin.model.internal.ClickPagosEntity;
import um.tesoreria.core.service.facade.ClickPagosService;

@RestController
@RequestMapping("/clickPagos")
public class ClickPagosController {

    @Autowired
    private ClickPagosService service;

    @PostMapping("/processLine/{verify}")
    public ResponseEntity<ClickPagosEntity> processLine(@RequestBody String line, @PathVariable Boolean verify) {
        return new ResponseEntity<>(service.processLine(line, verify), HttpStatus.OK);
    }
}
