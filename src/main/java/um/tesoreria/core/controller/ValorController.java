package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.kotlin.model.Valor;
import um.tesoreria.core.service.ValorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/valor")
@RequiredArgsConstructor
public class ValorController {

    private final ValorService service;

    @GetMapping("/")
    public ResponseEntity<List<Valor>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

}
