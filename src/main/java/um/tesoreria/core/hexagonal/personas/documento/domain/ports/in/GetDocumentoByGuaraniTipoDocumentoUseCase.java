package um.tesoreria.core.hexagonal.personas.documento.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.documento.domain.model.Documento;

import java.util.Optional;

public interface GetDocumentoByGuaraniTipoDocumentoUseCase {

    Optional<Documento> getDocumentoByGuaraniTipoDocumento(Integer guaraniTipoDocumento);

}
