package um.tesoreria.rest.controller;

import um.tesoreria.rest.kotlin.model.Valor;
import um.tesoreria.rest.service.ValorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/valor")
public class ValorController {

    @Autowired
    private ValorService service;

    @GetMapping("/")
    public ResponseEntity<List<Valor>> findAll() {
        return new ResponseEntity<List<Valor>>(service.findAll(), HttpStatus.OK);
    }

}
