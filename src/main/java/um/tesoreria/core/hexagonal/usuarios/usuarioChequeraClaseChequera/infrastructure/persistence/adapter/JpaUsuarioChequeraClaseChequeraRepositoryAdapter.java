package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.ports.out.UsuarioChequeraClaseChequeraRepository;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.mapper.UsuarioChequeraClaseChequeraMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.repository.JpaUsuarioChequeraClaseChequeraRepository;

import java.util.List;
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
}
