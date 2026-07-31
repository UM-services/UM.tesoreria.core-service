package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.service.BancoMovimientoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bancoMovimiento")
@RequiredArgsConstructor
public class BancoMovimientoController {

    private final BancoMovimientoService service;

}
