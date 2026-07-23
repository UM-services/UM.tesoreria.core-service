package um.tesoreria.core.hexagonal.setup.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.hexagonal.setup.application.service.SetupService;
import um.tesoreria.core.hexagonal.setup.infrastructure.web.dto.SetupResponse;
import um.tesoreria.core.hexagonal.setup.infrastructure.web.mapper.SetupDtoMapper;

@RestController
@RequestMapping({"/setup", "/api/tesoreria/core/setup"})
@RequiredArgsConstructor
public class SetupController {

    private final SetupService setupService;
    private final SetupDtoMapper setupDtoMapper;

    @GetMapping("/last")
    public ResponseEntity<SetupResponse> findLast() {
        return setupService.findLast()
                .map(domain -> ResponseEntity.ok(setupDtoMapper.toResponse(domain)))
                .orElse(ResponseEntity.notFound().build());
    }
}
