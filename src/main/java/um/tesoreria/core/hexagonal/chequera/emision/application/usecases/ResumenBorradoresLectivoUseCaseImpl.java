package um.tesoreria.core.hexagonal.chequera.emision.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.BorradorConsultaService;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.ResumenBorradoresLectivoUseCase;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto.FacultadSedeBorradorResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResumenBorradoresLectivoUseCaseImpl implements ResumenBorradoresLectivoUseCase {

    private final BorradorConsultaService service;

    @Override
    public List<FacultadSedeBorradorResponse> resumenLectivo(Integer lectivoId) {
        return service.resumenLectivo(lectivoId);
    }
}
