package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.DeletePagosByArchivoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

@Component
@RequiredArgsConstructor
public class DeletePagosByArchivoUseCaseImpl implements DeletePagosByArchivoUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public void deletePagosByArchivo(String archivo) {
        repository.deleteAllByArchivo(archivo);
    }

}
