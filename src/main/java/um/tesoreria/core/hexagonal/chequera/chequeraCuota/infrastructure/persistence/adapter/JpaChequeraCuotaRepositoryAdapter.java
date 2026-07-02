package um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.DeudaData;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.entity.ChequeraCuotaEntity;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.mapper.ChequeraCuotaMapper;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.repository.JpaChequeraCuotaRepository;
import um.tesoreria.core.model.internal.CuotaPeriodoDto;
import um.tesoreria.core.repository.ChequeraPagoRepository;
import um.tesoreria.core.repository.ChequeraTotalRepository;
import um.tesoreria.core.util.Jsonifier;
import um.tesoreria.core.util.Tool;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JpaChequeraCuotaRepositoryAdapter implements ChequeraCuotaRepository {

    private final JpaChequeraCuotaRepository jpaChequeraCuotaRepository;
    private final ChequeraPagoRepository chequeraPagoRepository;
    private final ChequeraTotalRepository chequeraTotalRepository;
    private final ChequeraCuotaMapper mapper;

    @Override
    public DeudaData findDeudaData(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        var cuotas = jpaChequeraCuotaRepository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
                        facultadId, tipoChequeraId, chequeraSerieId, alternativaId, (byte) 0, (byte) 0,
                        Tool.hourAbsoluteArgentina(), BigDecimal.ZERO)
                .stream()
                .map(mapper::toDomain)
                .toList();

        var pagos = chequeraPagoRepository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .stream()
                .map(mapper::toDomain)
                .toList();

        var totals = chequeraTotalRepository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .stream()
                .map(mapper::toDomain)
                .toList();

        return DeudaData.builder()
                .cuotas(cuotas)
                .pagos(pagos)
                .totals(totals)
                .build();
    }

    @Override
    public Optional<ChequeraCuota> findCuota1(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return jpaChequeraCuotaRepository
                .findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndCuotaIdOrderByProductoIdDesc(
                        facultadId, tipoChequeraId, chequeraSerieId, alternativaId, 1)
                .map(mapper::toDomain);
    }

    @Override
    public List<ChequeraCuota> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return jpaChequeraCuotaRepository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ChequeraCuota> findAllByChequeraAlternativa(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        log.debug("\n\nProcessing JpaChequeraCuotaRepositoryAdapter.findAllByChequeraAlternativa\n\n");
        var cuotas = jpaChequeraCuotaRepository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(
                facultadId, tipoChequeraId, chequeraSerieId, alternativaId, Sort.by("vencimiento1").ascending()
                        .and(Sort.by("productoId").ascending().and(Sort.by("cuotaId").ascending()))
        );
        log.debug("\n\nJpaChequeraCuotaRepositoryAdapter.findAllByChequeraAlternativa.cuotas -> {}\n\n", Jsonifier.builder(cuotas).build());
        return cuotas.stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ChequeraCuota> findAllByChequeraAlternativaConImporte(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return jpaChequeraCuotaRepository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndImporte1GreaterThan(
                facultadId, tipoChequeraId, chequeraSerieId, alternativaId, (byte) 0, BigDecimal.ZERO,
                Sort.by("vencimiento1").ascending()
                        .and(Sort.by("productoId").ascending().and(Sort.by("cuotaId").ascending()))
        ).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ChequeraCuota> findAllByCuotasActivas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Byte baja) {
        return jpaChequeraCuotaRepository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndBaja(
                facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, baja
        ).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ChequeraCuota> findAllByCuotasPagadas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Byte pagado) {
        return jpaChequeraCuotaRepository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndPagado(
                facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, pagado
        ).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ChequeraCuota> findAllDebidas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, OffsetDateTime referencia) {
        return jpaChequeraCuotaRepository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
                facultadId, tipoChequeraId, chequeraSerieId, alternativaId, (byte) 0, (byte) 0, referencia, BigDecimal.ZERO
        ).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ChequeraCuota> findAllPendientes(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return jpaChequeraCuotaRepository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndPagadoAndBajaAndCompensadaAndImporte1GreaterThan(
                facultadId, tipoChequeraId, chequeraSerieId, alternativaId, (byte) 0, (byte) 0, (byte) 0, BigDecimal.ZERO
        ).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ChequeraCuota> findAllPendientesBaja(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return jpaChequeraCuotaRepository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndPagadoAndBaja(
                facultadId, tipoChequeraId, chequeraSerieId, alternativaId, (byte) 0, (byte) 0
        ).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<ChequeraCuota> findByChequeraCuotaId(Long chequeraCuotaId) {
        return jpaChequeraCuotaRepository.findByChequeraCuotaId(chequeraCuotaId).map(mapper::toDomain);
    }

    @Override
    public Optional<ChequeraCuota> findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId) {
        return jpaChequeraCuotaRepository.findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(
                facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId
        ).map(mapper::toDomain);
    }

    @Override
    public Optional<ChequeraCuota> findOneActivaImpagaPrevia(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, OffsetDateTime referencia) {
        return jpaChequeraCuotaRepository.findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndImporte1GreaterThanAndVencimiento1LessThanOrderByVencimiento1Desc(
                facultadId, tipoChequeraId, chequeraSerieId, alternativaId, (byte) 0, (byte) 0, BigDecimal.ZERO, referencia
        ).map(mapper::toDomain);
    }

    @Override
    public List<ChequeraCuota> saveAll(List<ChequeraCuota> chequeraCuotas) {
        return chequeraCuotas.stream().map(this::save).toList();
    }

    @Override
    public ChequeraCuota save(ChequeraCuota chequeraCuota) {
        if (chequeraCuota.getChequeraCuotaId() != null) {
            var existingOpt = jpaChequeraCuotaRepository.findByChequeraCuotaId(chequeraCuota.getChequeraCuotaId());
            if (existingOpt.isPresent()) {
                ChequeraCuotaEntity entity = existingOpt.get();
                mapper.updateEntity(chequeraCuota, entity);
                return mapper.toDomain(jpaChequeraCuotaRepository.save(entity));
            }
        }
        ChequeraCuotaEntity entity = mapper.toEntity(chequeraCuota);
        return mapper.toDomain(jpaChequeraCuotaRepository.save(entity));
    }

    @Override
    public void deleteAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        jpaChequeraCuotaRepository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);
    }

    @Override
    public List<CuotaPeriodoDto> findCuotaPeriodosByLectivoId(Integer lectivoId) {
        return jpaChequeraCuotaRepository.findCuotaPeriodosByLectivoId(lectivoId);
    }

    @Override
    public List<ChequeraCuota> findAllByChequeraIds(List<Long> chequeraIds) {
        return jpaChequeraCuotaRepository.findAllByChequeraIdIn(chequeraIds).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<ChequeraCuota> findAllByFacultadTipoChequeraSerieIds(Integer facultadId, Integer tipoChequeraId, List<Long> chequeraSerieIds) {
        return jpaChequeraCuotaRepository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdIn(facultadId, tipoChequeraId, chequeraSerieIds)
                .stream().map(mapper::toDomain).toList();
    }
}
