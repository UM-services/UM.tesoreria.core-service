package um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.out.ClaseChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.mapper.ClaseChequeraMapper;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.repository.JpaClaseChequeraRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaClaseChequeraRepositoryAdapter implements ClaseChequeraRepository {

    private final JpaClaseChequeraRepository jpaClaseChequeraRepository;
    private final ClaseChequeraMapper claseChequeraMapper;

    @Override
    public List<ClaseChequera> findAll() {
        return jpaClaseChequeraRepository.findAll().stream()
                .map(claseChequeraMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClaseChequera> findAllByPosgrado(Byte posgrado) {
        return jpaClaseChequeraRepository.findAllByPosgrado(posgrado).stream()
                .map(claseChequeraMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClaseChequera> findAllByCurso(Byte curso) {
        return jpaClaseChequeraRepository.findAllByCurso(curso).stream()
                .map(claseChequeraMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClaseChequera> findAllByTitulo(Byte titulo) {
        return jpaClaseChequeraRepository.findAllByTitulo(titulo).stream()
                .map(claseChequeraMapper::toDomainModel)
                .collect(Collectors.toList());
    }

}
