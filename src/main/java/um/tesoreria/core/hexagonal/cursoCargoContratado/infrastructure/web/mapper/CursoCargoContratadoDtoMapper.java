package um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.web.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.client.haberes.core.CargoTipoClient;
import um.tesoreria.core.client.haberes.core.CursoClient;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model.CursoCargoContratado;
import um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.web.dto.CursoCargoContratadoResponse;
import um.tesoreria.core.service.ContratoService;

@Component
@Slf4j
@RequiredArgsConstructor
public class CursoCargoContratadoDtoMapper {

    private final CursoClient cursoClient;
    private final ContratoService contratoService;
    private final CargoTipoClient cargoTipoClient;

    public CursoCargoContratadoResponse toResponse(CursoCargoContratado cursoCargoContratado) {
        if (cursoCargoContratado == null) {
            return null;
        }
        var curso = cursoClient.findByCursoId(cursoCargoContratado.getCursoId());
        var cargoTipo = cargoTipoClient.findByCargoTipoId(cursoCargoContratado.getCargoTipoId());
        var contrato = contratoService.findByContratoId(cursoCargoContratado.getContratoId());
        var cursoCargoContratadoResponse = CursoCargoContratadoResponse.builder()
                .cursoCargoContratadoId(cursoCargoContratado.getCursoCargoContratadoId())
                .curso(curso)
                .anho(cursoCargoContratado.getAnho())
                .mes(cursoCargoContratado.getMes())
                .contrato(contrato)
                .cargoTipo(cargoTipo)
                .horasSemanales(cursoCargoContratado.getHorasSemanales())
                .horasTotales(cursoCargoContratado.getHorasTotales())
                .acreditado(cursoCargoContratado.getAcreditado())
                .build();
        log.debug("CursoCargoContratadoResponse: {}", cursoCargoContratadoResponse.jsonify());
        return cursoCargoContratadoResponse;
    }

}
