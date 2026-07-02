package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.mapper.ChequeraSerieMapper;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.repository.JpaChequeraSerieRepository;
import um.tesoreria.core.model.internal.FacultadSedeChequeraDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaChequeraSerieRepositoryAdapter implements ChequeraSerieRepository {

    private final JpaChequeraSerieRepository jpaRepository;
    private final ChequeraSerieMapper mapper;

    @Override
    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId, Sort sort) {
        return jpaRepository.findAllByPersonaIdAndDocumentoId(personaId, documentoId, sort).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraSerie> findAllByFacultadIdAndChequeraSerieId(Integer facultadId, Long chequeraSerieId, Sort sort) {
        return jpaRepository.findAllByFacultadIdAndChequeraSerieId(facultadId, chequeraSerieId, sort).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndLectivoIdAndFacultadId(BigDecimal personaId,
                                                                                               Integer documentoId, Integer lectivoId, Integer facultadId) {
        return jpaRepository.findAllByPersonaIdAndDocumentoIdAndLectivoIdAndFacultadId(personaId, documentoId, lectivoId, facultadId).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraSerie> findAllByLectivoIdAndFacultadId(Integer lectivoId, Integer facultadId) {
        return jpaRepository.findAllByLectivoIdAndFacultadId(lectivoId, facultadId).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraSerie> findAllByFacultadIdAndLectivoIdAndGeograficaId(Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return jpaRepository.findAllByFacultadIdAndLectivoIdAndGeograficaId(facultadId, lectivoId, geograficaId).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraSerie> findAllByLectivoIdAndFacultadIdAndPersonaId(Integer lectivoId, Integer facultadId, BigDecimal personaId) {
        return jpaRepository.findAllByLectivoIdAndFacultadIdAndPersonaId(lectivoId, facultadId, personaId).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraSerie> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId, Integer facultadId, Integer geograficaId) {
        return jpaRepository.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraSerie> findAllByGeograficaIdAndFacultadIdInAndLectivoIdIn(Integer geograficaId, List<Integer> facultadIds, List<Integer> lectivoIds) {
        return jpaRepository.findAllByGeograficaIdAndFacultadIdInAndLectivoIdIn(geograficaId, facultadIds, lectivoIds).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraSerie> findAllByFacultadIdAndLectivoIdAndTipoChequeraId(Integer facultadId, Integer lectivoId, Integer tipoChequeraId) {
        return jpaRepository.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(facultadId, lectivoId, tipoChequeraId).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndFacultadId(BigDecimal personaId, Integer documentoId, Integer facultadId) {
        return jpaRepository.findAllByPersonaIdAndDocumentoIdAndFacultadId(personaId, documentoId, facultadId).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId) {
        return jpaRepository.findAllByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoId(personaId, documentoId, facultadId, lectivoId).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraSerie> findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonaIdIn(Integer facultadId, Integer lectivoId, Integer geograficaId, List<BigDecimal> personaIds) {
        return jpaRepository.findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonaIdIn(facultadId, lectivoId, geograficaId, personaIds).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndLectivoId(BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        return jpaRepository.findAllByPersonaIdAndDocumentoIdAndLectivoId(personaId, documentoId, lectivoId).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ChequeraSerie> findFirstByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoId(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        return jpaRepository.findFirstByFacultadIdAndPersonaIdAndDocumentoIdAndLectivoId(facultadId, personaId, documentoId, lectivoId).map(mapper::toDomainModel);
    }

    @Override
    public Optional<ChequeraSerie> findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return jpaRepository.findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId).map(mapper::toDomainModel);
    }

    @Override
    public Optional<ChequeraSerie> findByChequeraId(Long chequeraId) {
        return jpaRepository.findByChequeraId(chequeraId).map(mapper::toDomainModel);
    }

    @Override
    public Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return jpaRepository.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaId(personaId, documentoId, facultadId, lectivoId, geograficaId).map(mapper::toDomainModel);
    }

    @Override
    public Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId, Integer tipoChequeraId) {
        return jpaRepository.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraId(personaId, documentoId, facultadId, lectivoId, geograficaId, tipoChequeraId).map(mapper::toDomainModel);
    }

    @Override
    public Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraIdIn(BigDecimal personaId, Integer documentoId, Integer facultadId, Integer lectivoId, Integer geograficaId, List<Integer> tipoChequeraIds) {
        return jpaRepository.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndLectivoIdAndGeograficaIdAndTipoChequeraIdIn(personaId, documentoId, facultadId, lectivoId, geograficaId, tipoChequeraIds).map(mapper::toDomainModel);
    }

    @Override
    public Optional<ChequeraSerie> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndTipoChequeraIdIn(BigDecimal personaId, Integer documentoId, Integer facultadId, List<Integer> tipoChequeraIds, Sort sort) {
        return jpaRepository.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndTipoChequeraIdIn(personaId, documentoId, facultadId, tipoChequeraIds, sort).map(mapper::toDomainModel);
    }

    @Override
    public List<ChequeraSerie> findAllByPersonaIdAndDocumentoIdAndFacultadIdAndTipoChequeraIdIn(BigDecimal personaId, Integer documentoId, Integer facultadId, List<Integer> tipoChequeraIds, Sort sort) {
        return jpaRepository.findAllByPersonaIdAndDocumentoIdAndFacultadIdAndTipoChequeraIdIn(personaId, documentoId, facultadId, tipoChequeraIds, sort).stream()
                .map(mapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        jpaRepository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);
    }

    @Override
    public List<FacultadSedeChequeraDto> findAllFacultadSedeByLectivo(Integer lectivoId) {
        return jpaRepository.findAllFacultadSedeByLectivo(lectivoId);
    }

    @Override
    public ChequeraSerie save(ChequeraSerie chequeraSerie) {
        ChequeraSerieEntity entity = mapper.toEntity(chequeraSerie);
        ChequeraSerieEntity saved = jpaRepository.save(entity);
        return mapper.toDomainModel(saved);
    }
}
