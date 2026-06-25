package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.application.service.AlumnoGuaraniService;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.AlumnoDeteccionRequest;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.AlumnoGuaraniRequest;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.mapper.AlumnoGuaraniDtoMapper;

import java.util.List;

@RestController
@RequestMapping("/api/tesoreria/core/guarani/alumno")
@RequiredArgsConstructor
@Slf4j
public class AlumnoGuaraniController {

    private final AlumnoGuaraniService alumnoGuaraniService;
    private final AlumnoGuaraniDtoMapper dtoMapper;

    @PostMapping("/create/preuniversitario")
    public ResponseEntity<AlumnoGuarani> createPreuniversitario(@RequestBody AlumnoGuaraniRequest request) {
        log.debug("\n\nProcessing AlumnoGuaraniController.createPreuniversitario\n\n");
        AlumnoGuarani domain = dtoMapper.toDomain(request);
        AlumnoGuarani created = alumnoGuaraniService.createPreuniversitario(domain);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/desmarcar/enviadas")
    public ResponseEntity<List<AlumnoDeteccionRequest>> desmarcarEnviados(@RequestBody List<AlumnoDeteccionRequest> encontrados) {
        List<AlumnoDeteccionRequest> result = alumnoGuaraniService.desmarcarEnviados(encontrados);
        return ResponseEntity.ok(result);
    }

}
