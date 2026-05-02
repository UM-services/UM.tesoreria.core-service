package um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.geografica.domain.ports.out.GeograficaRepository;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.entity.GeograficaEntity;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.mapper.GeograficaMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JpaGeograficaRepositoryAdapter implements GeograficaRepository {

    private final JpaGeograficaRepository jpaGeograficaRepository;
    private final GeograficaMapper geograficaMapper;

    public JpaGeograficaRepositoryAdapter(JpaGeograficaRepository jpaGeograficaRepository, GeograficaMapper geograficaMapper) {
        this.jpaGeograficaRepository = jpaGeograficaRepository;
        this.geograficaMapper = geograficaMapper;
    }

    @Override
    public List<Geografica> findAll() {
        return jpaGeograficaRepository.findAll().stream()
                .map(geograficaMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Geografica> findByGeograficaId(Integer geograficaId) {
        return jpaGeograficaRepository.findByGeograficaId(geograficaId)
                .map(geograficaMapper::toDomainModel);
    }

    @Override
    public List<Geografica> findAllByGeograficaId(Integer geograficaId) {
        return jpaGeograficaRepository.findAllByGeograficaId(geograficaId).stream()
                .map(geograficaMapper::toDomainModel)
                .collect(Collectors.toList());
    }

}
