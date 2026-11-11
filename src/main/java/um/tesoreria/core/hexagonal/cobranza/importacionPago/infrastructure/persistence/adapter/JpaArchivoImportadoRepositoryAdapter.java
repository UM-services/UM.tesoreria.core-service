package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoImportado;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ArchivoImportadoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.mapper.ArchivoImportadoMapper;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.repository.JpaArchivoImportadoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaArchivoImportadoRepositoryAdapter implements ArchivoImportadoRepository {

    private final JpaArchivoImportadoRepository repository;
    private final ArchivoImportadoMapper mapper;

    @Override
    public Optional<ArchivoImportado> findById(Integer id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<ArchivoImportado> findByNombre(String nombre) {
        return repository.findByNombre(nombre).map(mapper::toDomain);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return repository.existsByNombre(nombre);
    }

    @Override
    public ArchivoImportado save(ArchivoImportado archivoImportado) {
        return mapper.toDomain(repository.save(mapper.toEntity(archivoImportado)));
    }

    @Override
    public void delete(ArchivoImportado archivoImportado) {
        repository.delete(mapper.toEntity(archivoImportado));
    }

    @Override
    public List<ArchivoImportado> findAllByNombreIn(Collection<String> nombres) {
        return repository.findAllByNombreIn(nombres).stream().map(mapper::toDomain).toList();
    }

}
