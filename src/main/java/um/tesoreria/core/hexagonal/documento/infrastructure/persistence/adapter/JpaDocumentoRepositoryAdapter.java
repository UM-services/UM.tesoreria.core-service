package um.tesoreria.core.hexagonal.documento.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.documento.domain.model.Documento;
import um.tesoreria.core.hexagonal.documento.domain.ports.out.DocumentoRepository;
import um.tesoreria.core.hexagonal.documento.infrastructure.persistence.mapper.DocumentoMapper;
import um.tesoreria.core.hexagonal.documento.infrastructure.persistence.repository.JpaDocumentoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaDocumentoRepositoryAdapter implements DocumentoRepository {

    private final JpaDocumentoRepository jpaDocumentoRepository;
    private final DocumentoMapper documentoMapper;

    @Override
    public List<Documento> findAll() {
        return jpaDocumentoRepository.findAll().stream()
                .map(documentoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Documento> findById(Integer documentoId) {
        return jpaDocumentoRepository.findByDocumentoId(documentoId)
                .map(documentoMapper::toDomainModel);
    }

}
