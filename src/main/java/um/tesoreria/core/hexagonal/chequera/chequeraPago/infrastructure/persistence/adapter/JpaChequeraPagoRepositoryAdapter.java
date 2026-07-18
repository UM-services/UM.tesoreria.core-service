package um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.entity.ChequeraCuotaEntity;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.persistence.entity.ChequeraPagoEntity;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.persistence.mapper.ChequeraPagoMapper;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.persistence.repository.JpaChequeraPagoRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaChequeraPagoRepositoryAdapter implements ChequeraPagoRepository {

    private final JpaChequeraPagoRepository jpaChequeraPagoRepository;
    private final ChequeraPagoMapper chequeraPagoMapper;
    private final ChequeraCuotaService chequeraCuotaService;

    @Override
    public List<ChequeraPago> findAllByTipoPagoAndAcreditacion(Integer tipoPagoId, OffsetDateTime fechaAcreditacion) {
        return jpaChequeraPagoRepository.findAllByTipoPagoIdAndAcreditacion(tipoPagoId, fechaAcreditacion).stream()
                .map(chequeraPagoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraPago> findAllByTipoPagoAndFechaBetween(Integer tipoPagoId, OffsetDateTime fechaInicio, OffsetDateTime fechaFin) {
        return jpaChequeraPagoRepository.findAllByTipoPagoIdAndFechaBetween(tipoPagoId, fechaInicio, fechaFin).stream()
                .map(chequeraPagoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraPago> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        List<ChequeraPagoEntity> pagos = jpaChequeraPagoRepository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);
        pagos.forEach(this::fillChequeraCuotaId);
        return jpaChequeraPagoRepository.saveAll(pagos).stream()
                .map(chequeraPagoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraPago> findAllByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                              Integer productoId, Integer alternativaId, Integer cuotaId) {
        List<ChequeraPagoEntity> pagos = jpaChequeraPagoRepository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(
                        facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
        pagos.forEach(this::fillChequeraCuotaId);
        return jpaChequeraPagoRepository.saveAll(pagos).stream()
                .map(chequeraPagoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraPago> findPendientesFactura(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, Integer tipoPagoThreshold) {
        List<ChequeraPagoEntity> pagos = jpaChequeraPagoRepository
                .findAllByFechaBetweenAndTipoPagoIdGreaterThan(fechaInicio, fechaFin, tipoPagoThreshold);
        pagos.forEach(this::fillChequeraCuotaId);
        jpaChequeraPagoRepository.saveAll(pagos);

        List<ChequeraPagoEntity> validPagos = pagos.stream()
                .filter(this::isValidChequeraPago)
                .collect(Collectors.toList());

        return validPagos.stream()
                .map(chequeraPagoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChequeraPago> findAllByFacultadAndTipoChequeraAndLectivo(Integer facultadId, Integer tipoChequeraId, Integer lectivoId) {
        return jpaChequeraPagoRepository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraCuotaChequeraSerieLectivoId(facultadId, tipoChequeraId, lectivoId)
                .stream()
                .map(chequeraPagoMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ChequeraPago> findByChequeraPagoId(Long chequeraPagoId) {
        return jpaChequeraPagoRepository.findByChequeraPagoId(chequeraPagoId)
                .map(entity -> {
                    if (entity.getChequeraCuotaId() == null) {
                        fillChequeraCuotaId(entity);
                        return jpaChequeraPagoRepository.save(entity);
                    }
                    return entity;
                })
                .map(chequeraPagoMapper::toDomain);
    }

    @Override
    public Optional<ChequeraPago> findByIdMercadoPago(String idMercadoPago) {
        return jpaChequeraPagoRepository.findByIdMercadoPago(idMercadoPago)
                .map(chequeraPagoMapper::toDomain);
    }

    @Override
    public Optional<ChequeraPago> findTopByCompositeKeyOrderByOrdenDesc(Integer facultadId, Integer tipoChequeraId,
                                                                         Long chequeraSerieId, Integer productoId,
                                                                         Integer alternativaId, Integer cuotaId) {
        return jpaChequeraPagoRepository
                .findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaIdOrderByOrdenDesc(
                        facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId)
                .map(chequeraPagoMapper::toDomain);
    }

    @Override
    public void deleteAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        jpaChequeraPagoRepository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);
    }

    @Override
    public ChequeraPago save(ChequeraPago chequeraPago) {
        ChequeraPagoEntity entity = chequeraPagoMapper.toEntity(chequeraPago);
        fillChequeraCuotaId(entity);
        ChequeraPagoEntity saved = jpaChequeraPagoRepository.save(entity);
        return chequeraPagoMapper.toDomain(saved);
    }

    @Override
    public List<ChequeraPago> saveAll(List<ChequeraPago> chequeraPagos) {
        List<ChequeraPagoEntity> entities = chequeraPagos.stream()
                .map(chequeraPagoMapper::toEntity)
                .collect(Collectors.toList());
        entities.forEach(this::fillChequeraCuotaId);
        return jpaChequeraPagoRepository.saveAll(entities).stream()
                .map(chequeraPagoMapper::toDomain)
                .collect(Collectors.toList());
    }

    private void fillChequeraCuotaId(ChequeraPagoEntity pago) {
        if (pago.getChequeraCuotaId() == null) {
            pago.setChequeraCuotaId(chequeraCuotaService.findByUnique(
                    pago.getFacultadId(),
                    pago.getTipoChequeraId(),
                    pago.getChequeraSerieId(),
                    pago.getProductoId(),
                    pago.getAlternativaId(),
                    pago.getCuotaId()
            ).getChequeraCuotaId());
        }
    }

    private boolean isValidChequeraPago(ChequeraPagoEntity pago) {
        return Optional.ofNullable(pago.getChequeraCuota())
                .map(ChequeraCuotaEntity::getChequeraSerie)
                .map(ChequeraSerieEntity::getPersona)
                .isPresent();
    }

}
