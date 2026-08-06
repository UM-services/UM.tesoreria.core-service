package um.tesoreria.core.hexagonal.personas.documento.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.personas.documento.application.exception.DocumentoException;
import um.tesoreria.core.hexagonal.personas.documento.domain.model.Documento;
import um.tesoreria.core.hexagonal.personas.documento.domain.ports.in.GetAllDocumentosUseCase;
import um.tesoreria.core.hexagonal.personas.documento.domain.ports.in.GetDocumentoByGuaraniTipoDocumentoUseCase;
import um.tesoreria.core.hexagonal.personas.documento.domain.ports.in.GetDocumentoByIdUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final GetAllDocumentosUseCase getAllDocumentosUseCase;
    private final GetDocumentoByIdUseCase getDocumentoByIdUseCase;
    private final GetDocumentoByGuaraniTipoDocumentoUseCase getDocumentoByGuaraniTipoDocumentoUseCase;

    public List<Documento> findAll() {
        return getAllDocumentosUseCase.getAllDocumentos();
    }

    public Documento findByDocumentoId(Integer documentoId) {
        return getDocumentoByIdUseCase.getDocumentoById(documentoId)
                .orElseThrow(() -> new DocumentoException(documentoId));
    }

    public Documento findFirstByGuaraniTipoDocumento(Integer guaraniTipoDocumento) {
        return getDocumentoByGuaraniTipoDocumentoUseCase.getDocumentoByGuaraniTipoDocumento(guaraniTipoDocumento)
                .orElseThrow(() -> new DocumentoException(String.valueOf(guaraniTipoDocumento)));
    }

}
