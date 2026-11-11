package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out;

import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoImportado;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArchivoImportadoRepository {

    Optional<ArchivoImportado> findById(Integer id);

    Optional<ArchivoImportado> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    ArchivoImportado save(ArchivoImportado archivoImportado);

    void delete(ArchivoImportado archivoImportado);

    List<ArchivoImportado> findAllByNombreIn(Collection<String> nombres);

}
