package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.out.UsuarioChequeraFacultadRepository;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.entity.UsuarioChequeraFacultadEntity;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.mapper.UsuarioChequeraFacultadMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.repository.JpaUsuarioChequeraFacultadRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaUsuarioChequeraFacultadRepositoryAdapter implements UsuarioChequeraFacultadRepository {

    private final JpaUsuarioChequeraFacultadRepository repository;
    private final UsuarioChequeraFacultadMapper mapper;

    @Override
    public List<UsuarioChequeraFacultad> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioChequeraFacultad> findByUserIdAndFacultadId(Long userId, Integer facultadId) {
        return repository.findByUserIdAndFacultadId(userId, facultadId).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public UsuarioChequeraFacultad save(UsuarioChequeraFacultad usuarioChequeraFacultad) {
        UsuarioChequeraFacultadEntity saved = repository.save(mapper.toEntity(usuarioChequeraFacultad));
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndFacultadId(Long userId, Integer facultadId) {
        repository.deleteByUserIdAndFacultadId(userId, facultadId);
    }
}
