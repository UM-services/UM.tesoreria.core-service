package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.FindPagosByFacultadAndTipoChequeraAndLectivoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindPagosByFacultadAndTipoChequeraAndLectivoUseCaseImpl implements FindPagosByFacultadAndTipoChequeraAndLectivoUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public List<ChequeraPago> findPagosByFacultadAndTipoChequeraAndLectivo(Integer facultadId, Integer tipoChequeraId, Integer lectivoId) {
        return repository.findAllByFacultadAndTipoChequeraAndLectivo(facultadId, tipoChequeraId, lectivoId);
    }

}
