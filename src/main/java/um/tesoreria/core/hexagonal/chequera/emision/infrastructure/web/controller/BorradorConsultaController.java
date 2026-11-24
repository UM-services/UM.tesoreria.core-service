package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.ListarBorradoresSedeUseCase;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.ResumenBorradoresLectivoUseCase;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.BorradorResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.FacultadSedeBorradorResponse;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.mapper.EmisionDtoMapper;

import java.util.List;

/**
 * Consultas de borradores para la exportación por facultad y sede.
 *
 * <p>Sin {@code @RequierePermiso}: las consume {@code tesoreria-report} servicio-a-servicio, igual
 * que {@code /borradores/{id}/impresion}. El gating lo hace la pantalla con
 * {@code chequeras.consultas.detalleCuotasBorrador}.</p>
 */
@RestController
@RequestMapping("/api/tesoreria/core/emision/chequera/borradores")
@RequiredArgsConstructor
public class BorradorConsultaController {

    private final ResumenBorradoresLectivoUseCase resumenBorradoresLectivoUseCase;
    private final ListarBorradoresSedeUseCase listarBorradoresSedeUseCase;
    private final EmisionDtoMapper mapper;

    @GetMapping("/resumen/lectivo/{lectivoId}")
    public ResponseEntity<List<FacultadSedeBorradorResponse>> resumenLectivo(
            @PathVariable Integer lectivoId) {
        return ResponseEntity.ok(resumenBorradoresLectivoUseCase.resumenLectivo(lectivoId));
    }

    @GetMapping("/sede/facultad/{facultadId}/lectivo/{lectivoId}/geografica/{geograficaId}")
    public ResponseEntity<List<BorradorResponse>> listarPorSede(@PathVariable Integer facultadId,
                                                                @PathVariable Integer lectivoId,
                                                                @PathVariable Integer geograficaId) {
        return ResponseEntity.ok(listarBorradoresSedeUseCase.listar(facultadId, lectivoId, geograficaId).stream()
                .map(mapper::toResponse).toList());
    }
}
