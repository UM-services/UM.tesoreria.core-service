package um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.out.LectivoRepository;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.entity.LectivoEntity;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.mapper.LectivoMapper;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.repository.JpaLectivoRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.repository.JpaChequeraSerieRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaLectivoRepositoryAdapter implements LectivoRepository {

    private final JpaLectivoRepository jpaLectivoRepository;
    private final JpaChequeraSerieRepository chequeraSerieRepository;
    private final LectivoMapper lectivoMapper;

    @Override
    public List<Lectivo> findAll() {
        return jpaLectivoRepository.findAll(Sort.by("lectivoId").ascending()).stream()
                .map(lectivoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lectivo> findAllReverse() {
        return jpaLectivoRepository.findAll(Sort.by("lectivoId").descending()).stream()
                .map(lectivoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Lectivo> findAllByPersona(BigDecimal personaId, Integer documentoId) {
        return jpaLectivoRepository.findAllByLectivoIdIn(
                chequeraSerieRepository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, null).stream()
                        .map(ChequeraSerieEntity::getLectivoId)
                        .collect(Collectors.toList()),
                Sort.by("lectivoId").descending()).stream()
                .map(lectivoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Lectivo> findById(Integer lectivoId) {
        return jpaLectivoRepository.findByLectivoId(lectivoId).map(lectivoMapper::toDomainModel);
    }

    @Override
    public Optional<Lectivo> findByFecha(OffsetDateTime fecha) {
        return jpaLectivoRepository.findByFechaInicioLessThanEqualAndFechaFinalGreaterThanEqual(fecha, fecha)
                .map(lectivoMapper::toDomainModel);
    }

    @Override
    public Optional<Lectivo> findLast() {
        return jpaLectivoRepository.findTopByOrderByLectivoIdDesc().map(lectivoMapper::toDomainModel);
    }

    @Override
    public Lectivo create(Lectivo lectivo) {
        LectivoEntity entity = lectivoMapper.toEntity(lectivo);
        LectivoEntity saved = jpaLectivoRepository.save(entity);
        return lectivoMapper.toDomainModel(saved);
    }

    @Override
    public void deleteById(Integer lectivoId) {
        jpaLectivoRepository.deleteById(lectivoId);
    }
}
