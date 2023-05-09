package ar.edu.um.tesoreria.rest.controller;

import ar.edu.um.tesoreria.rest.kotlin.model.Bancaria;
import ar.edu.um.tesoreria.rest.service.BancariaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bancaria")
public class BancariaController {

    @Autowired
    private BancariaService service;

    @GetMapping("/")
    public ResponseEntity<List<Bancaria>> findAll() {
        return new ResponseEntity<List<Bancaria>>(service.findAll(), HttpStatus.OK);
    }

}
