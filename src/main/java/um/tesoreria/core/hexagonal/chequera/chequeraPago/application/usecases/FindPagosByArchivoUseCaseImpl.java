package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.FindPagosByArchivoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindPagosByArchivoUseCaseImpl implements FindPagosByArchivoUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public List<ChequeraPago> findPagosByArchivo(String archivo) {
        return repository.findAllByArchivo(archivo);
    }

}
