package um.tesoreria.core.hexagonal.chequeraCuota.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.FacultadException;
import um.tesoreria.core.exception.LectivoException;
import um.tesoreria.core.exception.TipoChequeraException;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.DeudaData;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.kotlin.model.Facultad;
import um.tesoreria.core.kotlin.model.Lectivo;
import um.tesoreria.core.kotlin.model.TipoChequera;
import um.tesoreria.core.model.dto.DeudaChequeraDto;
import um.tesoreria.core.service.FacultadService;
import um.tesoreria.core.service.LectivoService;
import um.tesoreria.core.service.TipoChequeraService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service("hexagonalChequeraCuotaService")
@RequiredArgsConstructor
@Slf4j
public class ChequeraCuotaService implements CalculateDeudaUseCase {

    private final ChequeraCuotaRepository repository;
    private final FacultadService facultadService;
    private final TipoChequeraService tipoChequeraService;
    private final LectivoService lectivoService;

    @Override
    public DeudaChequeraDto calculateDeuda(ChequeraSerie serie) {
        if (serie == null) {
            return createDefaultDeudaChequera(null, null, null);
        }

        DeudaData deudaData = repository.findDeudaData(serie.getFacultadId(), serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId());
        Optional<ChequeraCuota> cuota1 = repository.findCuota1(serie.getFacultadId(), serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId());

        return calculateDeudaInternal(serie, deudaData, cuota1, false);
    }

    @Override
    public DeudaChequeraDto calculateDeudaExtended(ChequeraSerie serie) {
        if (serie == null) {
            return createDefaultDeudaChequera(null, null, null);
        }

        DeudaData deudaData = repository.findDeudaData(serie.getFacultadId(), serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId());
        Optional<ChequeraCuota> cuota1 = repository.findCuota1(serie.getFacultadId(), serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId());

        return calculateDeudaInternal(serie, deudaData, cuota1, true);
    }

    private DeudaChequeraDto calculateDeudaInternal(ChequeraSerie serie, DeudaData deudaData, Optional<ChequeraCuota> cuota1, boolean extended) {
        Map<String, BigDecimal> pagosMap = deudaData.getPagos().stream()
                .collect(Collectors.toMap(
                        ChequeraPago::getCuotaKey,
                        ChequeraPago::getImporte,
                        (existing, replacement) -> existing));

        BigDecimal total = deudaData.getTotals().stream()
                .map(ChequeraTotal::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OffsetDateTime vencimiento1Cuota1 = null;
        BigDecimal importa1Cuota1 = BigDecimal.ZERO;
        if (cuota1.isPresent()) {
            vencimiento1Cuota1 = cuota1.get().getVencimiento1();
            importa1Cuota1 = cuota1.get().getImporte1();
        }

        // Calculate Deuda
        var deudaInfo = deudaData.getCuotas().stream()
                .map(cuota -> {
                    BigDecimal pagoAplicado = pagosMap.getOrDefault(cuota.cuotaKey(), BigDecimal.ZERO);
                    BigDecimal deudaCuota = cuota.getImporte1()
                            .subtract(pagoAplicado)
                            .max(BigDecimal.ZERO)
                            .setScale(2, RoundingMode.HALF_UP);
                    return new AbstractMap.SimpleEntry<>(deudaCuota,
                            deudaCuota.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0);
                })
                .reduce(
                        new AbstractMap.SimpleEntry<>(BigDecimal.ZERO, 0),
                        (acc, curr) -> new AbstractMap.SimpleEntry<>(
                                acc.getKey().add(curr.getKey()),
                                acc.getValue() + curr.getValue()));

        String facultadNombre = "";
        String tipoChequeraNombre = "";
        String lectivoNombre = "";

        if (extended) {
            try {
                facultadNombre = facultadService.findByFacultadId(serie.getFacultadId()).getNombre();
            } catch (FacultadException e) {
                // Ignore
            }
            try {
                tipoChequeraNombre = tipoChequeraService.findByTipoChequeraId(serie.getTipoChequeraId()).getNombre();
            } catch (TipoChequeraException e) {
                // Ignore
            }
            try {
                lectivoNombre = lectivoService.findByLectivoId(serie.getLectivoId()).getNombre();
            } catch (LectivoException e) {
                // Ignore
            }
        }

        return new DeudaChequeraDto(
                serie.getPersonaId(),
                serie.getDocumentoId(),
                serie.getFacultadId(),
                facultadNombre,
                serie.getTipoChequeraId(),
                tipoChequeraNombre,
                serie.getChequeraSerieId(),
                serie.getLectivoId(),
                lectivoNombre,
                serie.getAlternativaId(),
                total,
                deudaInfo.getKey(), // Deuda
                deudaInfo.getValue(), // Cuotas
                serie.getChequeraId(),
                vencimiento1Cuota1,
                importa1Cuota1);
    }

    private DeudaChequeraDto createDefaultDeudaChequera(Integer facultadId, Integer tipoChequeraId,
                                                        Long chequeraSerieId) {
        return new DeudaChequeraDto(
                BigDecimal.ZERO,
                0,
                facultadId,
                "", tipoChequeraId,
                "",
                chequeraSerieId,
                0,
                "",
                1,
                BigDecimal.ZERO,
                new BigDecimal(1000000),
                1000,
                null,
                OffsetDateTime.now(),
                BigDecimal.ZERO);
    }

}
