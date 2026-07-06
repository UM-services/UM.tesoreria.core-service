package um.tesoreria.core.hexagonal.documento.domain.ports.out;

import um.tesoreria.core.hexagonal.documento.domain.model.Documento;

import java.util.List;
import java.util.Optional;

public interface DocumentoRepository {

    List<Documento> findAll();
    Optional<Documento> findById(Integer documentoId);

}
