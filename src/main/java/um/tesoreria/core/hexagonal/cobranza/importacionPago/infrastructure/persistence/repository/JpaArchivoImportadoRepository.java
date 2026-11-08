package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.entity.ArchivoImportadoEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaArchivoImportadoRepository extends JpaRepository<ArchivoImportadoEntity, Integer> {

    Optional<ArchivoImportadoEntity> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<ArchivoImportadoEntity> findAllByNombreIn(Collection<String> nombres);

}
