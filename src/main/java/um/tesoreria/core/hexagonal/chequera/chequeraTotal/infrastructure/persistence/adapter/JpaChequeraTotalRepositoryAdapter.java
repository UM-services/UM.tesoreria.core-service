package um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.out.ChequeraTotalRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.persistence.entity.ChequeraTotalEntity;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.persistence.mapper.ChequeraTotalMapper;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.persistence.repository.JpaChequeraTotalRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaChequeraTotalRepositoryAdapter implements ChequeraTotalRepository {

    private final JpaChequeraTotalRepository jpaChequeraTotalRepository;
    private final ChequeraTotalMapper mapper;

    @Override
    public List<ChequeraTotal> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return jpaChequeraTotalRepository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ChequeraTotal> findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId) {
        return jpaChequeraTotalRepository.findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoId(facultadId, tipoChequeraId, chequeraSerieId, productoId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<ChequeraTotal> findByChequeraTotalId(Long chequeraTotalId) {
        return jpaChequeraTotalRepository.findByChequeraTotalId(chequeraTotalId)
                .map(mapper::toDomain);
    }

    @Override
    public List<ChequeraTotal> saveAll(List<ChequeraTotal> chequeraTotals) {
        List<ChequeraTotalEntity> entities = chequeraTotals.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        return jpaChequeraTotalRepository.saveAll(entities).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public ChequeraTotal save(ChequeraTotal chequeraTotal) {
        ChequeraTotalEntity entity = mapper.toEntity(chequeraTotal);
        ChequeraTotalEntity saved = jpaChequeraTotalRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public void deleteAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        jpaChequeraTotalRepository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);
    }
}
