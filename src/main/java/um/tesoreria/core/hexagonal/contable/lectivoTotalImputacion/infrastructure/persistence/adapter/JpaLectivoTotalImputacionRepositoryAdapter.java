package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.out.LectivoTotalImputacionRepository;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.entity.LectivoTotalImputacionEntity;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.mapper.LectivoTotalImputacionMapper;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.persistence.repository.JpaLectivoTotalImputacionRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaLectivoTotalImputacionRepositoryAdapter implements LectivoTotalImputacionRepository {

    private final JpaLectivoTotalImputacionRepository jpaLectivoTotalImputacionRepository;
    private final LectivoTotalImputacionMapper mapper;

    @Override
    public List<LectivoTotalImputacion> findAllByTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId) {
        return jpaLectivoTotalImputacionRepository.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(facultadId, lectivoId, tipoChequeraId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<LectivoTotalImputacion> findAllByLectivo(Integer lectivoId) {
        return jpaLectivoTotalImputacionRepository.findAllByLectivoId(lectivoId)
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<LectivoTotalImputacion> findByProducto(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId) {
        return jpaLectivoTotalImputacionRepository
                .findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoId(facultadId, lectivoId, tipoChequeraId, productoId)
                .map(mapper::toDomain);
    }

    @Override
    public LectivoTotalImputacion add(LectivoTotalImputacion lectivoTotalImputacion) {
        LectivoTotalImputacionEntity entity = mapper.toEntity(lectivoTotalImputacion);
        LectivoTotalImputacionEntity saved = jpaLectivoTotalImputacionRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<LectivoTotalImputacion> update(Long id, LectivoTotalImputacion lectivoTotalImputacion) {
        if (jpaLectivoTotalImputacionRepository.existsById(id)) {
            LectivoTotalImputacionEntity entity = mapper.toEntity(lectivoTotalImputacion);
            entity.setLectivoTotalImputacionId(id);
            LectivoTotalImputacionEntity updated = jpaLectivoTotalImputacionRepository.save(entity);
            return Optional.of(mapper.toDomain(updated));
        }
        return Optional.empty();
    }

    @Override
    public Optional<LectivoTotalImputacion> findById(Long id) {
        return jpaLectivoTotalImputacionRepository.findById(id).map(mapper::toDomain);
    }

}
