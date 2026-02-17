package um.tesoreria.core.hexagonal.chequeraCuota.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.DeudaData;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequeraCuota.infrastructure.persistence.mapper.ChequeraCuotaMapper;
import um.tesoreria.core.repository.ChequeraPagoRepository;
import um.tesoreria.core.repository.ChequeraTotalRepository;
import um.tesoreria.core.util.Tool;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaChequeraCuotaRepositoryAdapter implements ChequeraCuotaRepository {

    private final um.tesoreria.core.repository.ChequeraCuotaRepository jpaChequeraCuotaRepository;
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
}
