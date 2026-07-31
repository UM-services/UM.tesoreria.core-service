package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.kotlin.model.Bancaria;
import um.tesoreria.core.service.BancariaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bancaria")
@RequiredArgsConstructor
public class BancariaController {

    private final BancariaService service;

    @GetMapping("/")
    public ResponseEntity<List<Bancaria>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

}
