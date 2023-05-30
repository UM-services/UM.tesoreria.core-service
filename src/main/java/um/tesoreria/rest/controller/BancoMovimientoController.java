package um.tesoreria.rest.controller;

import um.tesoreria.rest.service.BancoMovimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bancoMovimiento")
public class BancoMovimientoController {

    @Autowired
    private BancoMovimientoService service;

}
