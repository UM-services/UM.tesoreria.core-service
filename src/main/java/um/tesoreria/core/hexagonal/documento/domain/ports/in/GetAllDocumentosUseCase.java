package um.tesoreria.core.hexagonal.documento.domain.ports.in;

import um.tesoreria.core.hexagonal.documento.domain.model.Documento;
import java.util.List;

public interface GetAllDocumentosUseCase {

    List<Documento> getAllDocumentos();

}
