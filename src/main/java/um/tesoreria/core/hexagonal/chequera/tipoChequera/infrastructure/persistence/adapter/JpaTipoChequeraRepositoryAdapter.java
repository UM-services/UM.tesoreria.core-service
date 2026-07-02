package um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.out.TipoChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.entity.TipoChequeraEntity;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.mapper.TipoChequeraMapper;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.repository.JpaTipoChequeraRepository;
import um.tesoreria.core.service.LectivoTotalService;
import um.tesoreria.core.model.LectivoTotal;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaTipoChequeraRepositoryAdapter implements TipoChequeraRepository {

    private final JpaTipoChequeraRepository jpaTipoChequeraRepository;
    private final LectivoTotalService lectivoTotalService;
    private final TipoChequeraMapper tipoChequeraMapper;

    @Override
    public List<TipoChequera> findAll() {
        return jpaTipoChequeraRepository.findAll().stream()
                .map(tipoChequeraMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TipoChequera> findAllAsignable(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer claseChequeraId) {
        List<Integer> tipoChequeraIds = lectivoTotalService.findAllByLectivo(facultadId, lectivoId).stream()
                .map(LectivoTotal::getTipoChequeraId).distinct().collect(Collectors.toList());
        return jpaTipoChequeraRepository.findAllByTipoChequeraIdInAndGeograficaIdAndClaseChequeraId(
                tipoChequeraIds, geograficaId, claseChequeraId, Sort.by("nombre").ascending()).stream()
                .map(tipoChequeraMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TipoChequera> findAllByFacultadIdAndGeograficaId(Integer facultadId, Integer geograficaId) {
        List<Integer> tipoChequeraIds = lectivoTotalService.findAllByFacultadId(facultadId).stream()
                .map(LectivoTotal::getTipoChequeraId).distinct().collect(Collectors.toList());
        return jpaTipoChequeraRepository.findAllByTipoChequeraIdInAndGeograficaId(tipoChequeraIds, geograficaId).stream()
                .map(tipoChequeraMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TipoChequera> findAllByClaseChequeraId(Integer claseChequeraId) {
        return jpaTipoChequeraRepository.findAllByClaseChequeraId(claseChequeraId).stream()
                .map(tipoChequeraMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<TipoChequera> findAllByClaseChequeraIds(List<Integer> claseChequeraIds) {
        return jpaTipoChequeraRepository.findAllByClaseChequeraIdIn(claseChequeraIds).stream()
                .map(tipoChequeraMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TipoChequera> findById(Integer tipoChequeraId) {
        return jpaTipoChequeraRepository.findByTipoChequeraId(tipoChequeraId)
                .map(tipoChequeraMapper::toDomainModel);
    }

    @Override
    public Optional<TipoChequera> findLast() {
        return jpaTipoChequeraRepository.findTopByOrderByTipoChequeraIdDesc()
                .map(tipoChequeraMapper::toDomainModel);
    }

    @Override
    public TipoChequera create(TipoChequera tipoChequera) {
        TipoChequeraEntity entity = tipoChequeraMapper.toEntity(tipoChequera);
        TipoChequeraEntity saved = jpaTipoChequeraRepository.save(entity);
        return tipoChequeraMapper.toDomainModel(saved);
    }

    @Override
    public TipoChequera update(TipoChequera tipoChequera) {
        TipoChequeraEntity entity = tipoChequeraMapper.toEntity(tipoChequera);
        TipoChequeraEntity saved = jpaTipoChequeraRepository.save(entity);
        return tipoChequeraMapper.toDomainModel(saved);
    }

    @Override
    public void deleteById(Integer tipoChequeraId) {
        jpaTipoChequeraRepository.deleteByTipoChequeraId(tipoChequeraId);
    }

    @Override
    public void unmark() {
        List<TipoChequeraEntity> tipos = jpaTipoChequeraRepository.findAll();
        tipos.forEach(tipo -> tipo.setImprimir((byte) 0));
        jpaTipoChequeraRepository.saveAll(tipos);
    }

    @Override
    public TipoChequera mark(Integer tipoChequeraId, Byte imprimir) {
        return jpaTipoChequeraRepository.findByTipoChequeraId(tipoChequeraId)
                .map(entity -> {
                    entity.setImprimir(imprimir);
                    return tipoChequeraMapper.toDomainModel(jpaTipoChequeraRepository.save(entity));
                })
                .orElseThrow(() -> new RuntimeException("Cannot find TipoChequera " + tipoChequeraId));
    }
}
