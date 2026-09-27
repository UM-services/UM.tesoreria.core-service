package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.out.UsuarioChequeraClaseChequeraRepository;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.entity.UsuarioChequeraClaseChequeraEntity;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.mapper.UsuarioChequeraClaseChequeraMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.repository.JpaUsuarioChequeraClaseChequeraRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaUsuarioChequeraClaseChequeraRepositoryAdapter implements UsuarioChequeraClaseChequeraRepository {

    private final JpaUsuarioChequeraClaseChequeraRepository repository;
    private final UsuarioChequeraClaseChequeraMapper mapper;

    @Override
    public List<UsuarioChequeraClaseChequera> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioChequeraClaseChequera> findByUserIdAndClaseChequeraId(Long userId, Integer claseChequeraId) {
        return repository.findByUserIdAndClaseChequeraId(userId, claseChequeraId).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public UsuarioChequeraClaseChequera save(UsuarioChequeraClaseChequera usuarioChequeraClaseChequera) {
        UsuarioChequeraClaseChequeraEntity saved = repository.save(mapper.toEntity(usuarioChequeraClaseChequera));
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndClaseChequeraId(Long userId, Integer claseChequeraId) {
        repository.deleteByUserIdAndClaseChequeraId(userId, claseChequeraId);
    }
}
