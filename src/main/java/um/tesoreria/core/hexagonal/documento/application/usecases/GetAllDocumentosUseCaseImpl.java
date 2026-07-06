package um.tesoreria.core.hexagonal.documento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.documento.domain.model.Documento;
import um.tesoreria.core.hexagonal.documento.domain.ports.in.GetAllDocumentosUseCase;
import um.tesoreria.core.hexagonal.documento.domain.ports.out.DocumentoRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllDocumentosUseCaseImpl implements GetAllDocumentosUseCase {

    private final DocumentoRepository repository;

    @Override
    public List<Documento> getAllDocumentos() {
        return repository.findAll();
    }

}
