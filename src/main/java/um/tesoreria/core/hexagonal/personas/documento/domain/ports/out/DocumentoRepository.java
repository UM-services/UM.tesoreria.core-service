package um.tesoreria.core.hexagonal.personas.documento.domain.ports.out;

import um.tesoreria.core.hexagonal.personas.documento.domain.model.Documento;

import java.util.List;
import java.util.Optional;

public interface DocumentoRepository {

    List<Documento> findAll();
    Optional<Documento> findById(Integer documentoId);
    Optional<Documento> findFirstByGuaraniTipoDocumento(Integer guaraniTipoDocumento);

}
