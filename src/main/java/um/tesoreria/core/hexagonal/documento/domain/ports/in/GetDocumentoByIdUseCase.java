package um.tesoreria.core.hexagonal.documento.domain.ports.in;

import um.tesoreria.core.hexagonal.documento.domain.model.Documento;
import java.util.Optional;

public interface GetDocumentoByIdUseCase {

    Optional<Documento> getDocumentoById(Integer documentoId);

}
