package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.CreateBancariaUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.DeleteBancariaUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.GetBancariaByIdUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.GetBancariaUsoUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.in.UpdateBancariaUseCase;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.dto.BancariaRequest;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.dto.BancariaResponse;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.dto.BancariaUsoResponse;
import um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.mapper.BancariaDtoMapper;
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
    private final GetBancariaByIdUseCase getUseCase;
    private final GetBancariaUsoUseCase usoUseCase;
    private final CreateBancariaUseCase createUseCase;
    private final UpdateBancariaUseCase updateUseCase;
    private final DeleteBancariaUseCase deleteUseCase;
    private final BancariaDtoMapper dtoMapper;

    @GetMapping("/")
    public ResponseEntity<List<Bancaria>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Lecturas y escrituras nuevas: fachada del slice contable/bancaria; sus errores los responde BancariaExceptionAdvice.
    @GetMapping("/{bancariaId}")
    public ResponseEntity<BancariaResponse> findByBancariaId(@PathVariable Long bancariaId) {
        return ResponseEntity.ok(dtoMapper.toResponse(getUseCase.getBancariaById(bancariaId)));
    }

    @GetMapping("/{bancariaId}/uso")
    public ResponseEntity<BancariaUsoResponse> uso(@PathVariable Long bancariaId) {
        return ResponseEntity.ok(dtoMapper.toResponse(usoUseCase.getBancariaUso(bancariaId)));
    }

    @PostMapping("/")
    public ResponseEntity<BancariaResponse> add(@RequestBody BancariaRequest request) {
        return ResponseEntity.ok(dtoMapper.toResponse(createUseCase.createBancaria(dtoMapper.toDomain(request))));
    }

    @PutMapping("/{bancariaId}")
    public ResponseEntity<BancariaResponse> update(@RequestBody BancariaRequest request, @PathVariable Long bancariaId) {
        return ResponseEntity.ok(dtoMapper.toResponse(
                updateUseCase.updateBancaria(bancariaId, dtoMapper.toDomain(request))));
    }

    @DeleteMapping("/{bancariaId}")
    public ResponseEntity<Void> delete(@PathVariable Long bancariaId) {
        deleteUseCase.deleteBancaria(bancariaId);
        return ResponseEntity.noContent().build();
    }

}
