package um.tesoreria.core.hexagonal.personas.documento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.documento.domain.model.Documento;
import um.tesoreria.core.hexagonal.personas.documento.domain.ports.in.GetDocumentoByGuaraniTipoDocumentoUseCase;
import um.tesoreria.core.hexagonal.personas.documento.domain.ports.out.DocumentoRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetDocumentoByGuaraniTipoDocumentoUseCaseImpl implements GetDocumentoByGuaraniTipoDocumentoUseCase {

    private final DocumentoRepository documentoRepository;

    @Override
    public Optional<Documento> getDocumentoByGuaraniTipoDocumento(Integer guaraniTipoDocumento) {
        return documentoRepository.findFirstByGuaraniTipoDocumento(guaraniTipoDocumento);
    }

}
