package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.ports.out.UsuarioChequeraFacultadRepository;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.mapper.UsuarioChequeraFacultadMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.repository.JpaUsuarioChequeraFacultadRepository;

import java.util.List;
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
}
