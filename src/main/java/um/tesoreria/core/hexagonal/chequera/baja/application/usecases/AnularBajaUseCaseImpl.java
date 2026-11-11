package um.tesoreria.core.hexagonal.chequera.baja.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.AnularBajaService;
import um.tesoreria.core.hexagonal.chequera.baja.domain.model.ResultadoAnulacionBaja;
import um.tesoreria.core.hexagonal.chequera.baja.domain.ports.in.AnularBajaUseCase;

@Component
@RequiredArgsConstructor
public class AnularBajaUseCaseImpl implements AnularBajaUseCase {

    private final AnularBajaService service;

    @Override
    public ResultadoAnulacionBaja anular(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return service.anular(facultadId, tipoChequeraId, chequeraSerieId);
    }

}
