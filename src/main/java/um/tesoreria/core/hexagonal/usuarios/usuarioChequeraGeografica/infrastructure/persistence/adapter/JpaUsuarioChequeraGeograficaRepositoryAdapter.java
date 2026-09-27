package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.ports.out.UsuarioChequeraGeograficaRepository;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.entity.UsuarioChequeraGeograficaEntity;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.mapper.UsuarioChequeraGeograficaMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.repository.JpaUsuarioChequeraGeograficaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaUsuarioChequeraGeograficaRepositoryAdapter implements UsuarioChequeraGeograficaRepository {

    private final JpaUsuarioChequeraGeograficaRepository repository;
    private final UsuarioChequeraGeograficaMapper mapper;

    @Override
    public List<UsuarioChequeraGeografica> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioChequeraGeografica> findByUserIdAndGeograficaId(Long userId, Integer geograficaId) {
        return repository.findByUserIdAndGeograficaId(userId, geograficaId).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public UsuarioChequeraGeografica save(UsuarioChequeraGeografica usuarioChequeraGeografica) {
        UsuarioChequeraGeograficaEntity saved = repository.save(mapper.toEntity(usuarioChequeraGeografica));
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndGeograficaId(Long userId, Integer geograficaId) {
        repository.deleteByUserIdAndGeograficaId(userId, geograficaId);
    }
}
