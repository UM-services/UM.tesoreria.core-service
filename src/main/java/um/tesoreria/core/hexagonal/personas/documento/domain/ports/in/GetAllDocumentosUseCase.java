package um.tesoreria.core.hexagonal.personas.documento.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.documento.domain.model.Documento;
import java.util.List;

public interface GetAllDocumentosUseCase {

    List<Documento> getAllDocumentos();

}
