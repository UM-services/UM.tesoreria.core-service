package um.tesoreria.core.hexagonal.documento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.documento.domain.model.Documento;
import um.tesoreria.core.hexagonal.documento.domain.ports.in.GetDocumentoByIdUseCase;
import um.tesoreria.core.hexagonal.documento.domain.ports.out.DocumentoRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetDocumentoByIdUseCaseImpl implements GetDocumentoByIdUseCase {

    private final DocumentoRepository repository;

    @Override
    public Optional<Documento> getDocumentoById(Integer documentoId) {
        return repository.findById(documentoId);
    }

}
