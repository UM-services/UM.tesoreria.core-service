package um.tesoreria.core.hexagonal.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.client.tesoreria.mercadopago.PreferenceClient;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.hexagonal.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.GetDeudaPersonaUseCase;
import um.tesoreria.core.hexagonal.chequeraCuota.infrastructure.persistence.entity.ChequeraCuotaEntity;
import um.tesoreria.core.model.dto.DeudaChequeraDto;
import um.tesoreria.core.model.dto.DeudaPersonaDto;
import um.tesoreria.core.model.dto.VencimientoDto;
import um.tesoreria.core.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.service.MercadoPagoContextService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetDeudaPersonaUseCaseImpl implements GetDeudaPersonaUseCase {

    private final ChequeraSerieService chequeraSerieService;
    private final CalculateDeudaUseCase calculateDeudaUseCase;
    private final ChequeraCuotaService chequeraCuotaService;
    private final MercadoPagoContextService mercadoPagoContextService;
    private final PreferenceClient preferenceClient;
    private final um.tesoreria.core.service.facade.MercadoPagoCoreService mercadoPagoCoreService;

    @Override
    public DeudaPersonaDto deudaByPersona(BigDecimal personaId, Integer documentoId) {
        var deudaCuotas = 0;
        var deudaTotal = BigDecimal.ZERO;
        List<DeudaChequeraDto> deudas = new ArrayList<>();
        for (ChequeraSerieEntity chequera : chequeraSerieService.findAllByPersonaIdAndDocumentoId(personaId, documentoId,
                null)) {
            DeudaChequeraDto deuda = calculateDeudaUseCase.calculateDeuda(toDomain(chequera));
            if (deuda.getCuotas() > 0) {
                deudas.add(deuda);
                deudaCuotas += deuda.getCuotas();
                deudaTotal = deudaTotal.add(deuda.getDeuda()).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return DeudaPersonaDto.builder()
                .personaId(personaId)
                .documentoId(documentoId)
                .cuotas(deudaCuotas)
                .deuda(deudaTotal)
                .deudas(deudas)
                .vencimientos(new ArrayList<>())
                .build();
    }

    @Override
    public DeudaPersonaDto deudaByPersonaExtended(BigDecimal personaId, Integer documentoId) {
        log.debug("Processing deudaByPersonaExtended");
        var deudaCuotas = 0;
        var deudaTotal = BigDecimal.ZERO;
        List<DeudaChequeraDto> deudas = new ArrayList<>();
        List<VencimientoDto> vencimientoDtos = new ArrayList<>();

        // 1. Fetch all series for the person
        List<ChequeraSerieEntity> series = chequeraSerieService.findAllByPersonaIdAndDocumentoId(personaId, documentoId, null);

        // 2. Fetch all cuotas using business keys (Facultad, Tipo, Serie) to ensure we get them even if chequeraId is null
        List<ChequeraCuotaEntity> allCuotas = new ArrayList<>();
        Map<String, List<ChequeraSerieEntity>> seriesByGroup = series.stream()
                .collect(Collectors.groupingBy(s -> s.getFacultadId() + "-" + s.getTipoChequeraId()));

        for (Map.Entry<String, List<ChequeraSerieEntity>> entry : seriesByGroup.entrySet()) {
            List<ChequeraSerieEntity> groupSeries = entry.getValue();
            if (groupSeries.isEmpty()) continue;
            
            Integer facultadId = groupSeries.get(0).getFacultadId();
            Integer tipoChequeraId = groupSeries.get(0).getTipoChequeraId();
            List<Long> serieIds = groupSeries.stream().map(ChequeraSerieEntity::getChequeraSerieId).collect(Collectors.toList());

            allCuotas.addAll(chequeraCuotaService.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIds(
                    facultadId, tipoChequeraId, serieIds));
        }

        // 3. Batch "Self-Healing": Update chequeraId if null
        List<ChequeraCuotaEntity> cuotasToUpdate = new ArrayList<>();
        // Create a lookup map for series: key = "fac-tipo-serie"
        Map<String, ChequeraSerieEntity> seriesMap = series.stream()
                .collect(Collectors.toMap(
                        s -> s.getFacultadId() + "-" + s.getTipoChequeraId() + "-" + s.getChequeraSerieId(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        for (ChequeraCuotaEntity cuota : allCuotas) {
            String key = cuota.getFacultadId() + "-" + cuota.getTipoChequeraId() + "-" + cuota.getChequeraSerieId();
            ChequeraSerieEntity serie = seriesMap.get(key);
            
            if (serie != null) {
                // Ensure relationship is set in memory for processing
                cuota.setChequeraSerie(serie);
                
                // If persistent ID is missing, fix it
                if (cuota.getChequeraId() == null) {
                    cuota.setChequeraId(serie.getChequeraId());
                    cuotasToUpdate.add(cuota);
                }
            }
        }

        if (!cuotasToUpdate.isEmpty()) {
            log.info("Fixing {} cuotas with missing chequeraId", cuotasToUpdate.size());
            chequeraCuotaService.saveAll(cuotasToUpdate);
        }

        // 4. Group cuotas by chequeraId for processing
        Map<Long, List<ChequeraCuotaEntity>> cuotasByChequeraId = allCuotas.stream()
                .filter(c -> c.getChequeraId() != null)
                .collect(Collectors.groupingBy(ChequeraCuotaEntity::getChequeraId));

        // 5. Fetch MercadoPago contexts
        List<Long> allCuotaIds = allCuotas.stream()
                .map(ChequeraCuotaEntity::getChequeraCuotaId)
                .collect(Collectors.toList());

        Map<Long, MercadoPagoContext> contextsByCuotaId = mercadoPagoContextService.findAllByChequeraCuotaIds(allCuotaIds)
                .stream()
                .collect(Collectors.toMap(MercadoPagoContext::getChequeraCuotaId, Function.identity(), (a, b) -> a));

        // 6. Process debts
        for (ChequeraSerieEntity chequera : series) {
            DeudaChequeraDto deuda = calculateDeudaUseCase.calculateDeudaExtended(toDomain(chequera));
            if (deuda.getCuotas() > 0) {
                deudas.add(deuda);
                deudaCuotas += deuda.getCuotas();
                deudaTotal = deudaTotal.add(deuda.getDeuda()).setScale(2, RoundingMode.HALF_UP);
            }

            List<ChequeraCuotaEntity> currentCuotas = cuotasByChequeraId.getOrDefault(chequera.getChequeraId(), List.of());

            List<ChequeraCuotaEntity> filteredCuotas = currentCuotas.stream()
                    .filter(c -> Objects.equals(c.getAlternativaId(), chequera.getAlternativaId()))
                    .sorted(Comparator.comparing(ChequeraCuotaEntity::getVencimiento1, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(ChequeraCuotaEntity::getProductoId, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(ChequeraCuotaEntity::getCuotaId, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();

            for (ChequeraCuotaEntity chequeraCuota : filteredCuotas) {
                if (chequeraCuota.getPagado() == 0 && chequeraCuota.getBaja() == 0 && chequeraCuota.getCompensada() == 0
                        && chequeraCuota.getImporte1().compareTo(BigDecimal.ZERO) != 0) {

                    MercadoPagoContext existingContext = contextsByCuotaId.get(chequeraCuota.getChequeraCuotaId());
                    var umPreferenceMPDto = mercadoPagoCoreService.makeContext(chequeraCuota, existingContext);

                    if (umPreferenceMPDto != null) {
                        var context = umPreferenceMPDto.getMercadoPagoContext();
                        boolean needsUpdate = context.getPreferenceId() == null || context.getChanged() == 1;

                        if (needsUpdate) {
                            log.debug("Creando preferencia");
                            var mercadoPagoContextDto = preferenceClient.createPreference(umPreferenceMPDto);
                            // Update context with returned data
                            try {
                                context.setPreferenceId(mercadoPagoContextDto.getPreferenceId());
                                context.setInitPoint(mercadoPagoContextDto.getInitPoint());
                                context.setPreference(mercadoPagoContextDto.getPreference());
                                context.setLastVencimientoUpdated(OffsetDateTime.now(java.time.ZoneOffset.UTC));
                                context.setChanged((byte) 0);
                                mercadoPagoContextService.update(context, context.getMercadoPagoContextId());
                                contextsByCuotaId.put(chequeraCuota.getChequeraCuotaId(), context);
                            } catch (Exception e) {
                                log.error("Error updating context after preference creation", e);
                            }
                        }

                        // Formatear la fecha de vencimiento
                        String fechaVencimientoFormatted = context.getFechaVencimiento()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        vencimientoDtos.add(VencimientoDto.builder()
                                .chequeraCuotaId(chequeraCuota.getChequeraCuotaId())
                                .mercadoPagoContextId(context.getMercadoPagoContextId())
                                .producto(Objects.requireNonNull(chequeraCuota.getProducto()).getNombre())
                                .periodo(chequeraCuota.getMes() + "/" + chequeraCuota.getAnho())
                                .vencimiento(fechaVencimientoFormatted)
                                .importe(context.getImporte())
                                .initPoint(context.getInitPoint())
                                .build());
                    }
                }
            }
        }

        return DeudaPersonaDto.builder()
                .personaId(personaId)
                .documentoId(documentoId)
                .cuotas(deudaCuotas)
                .deuda(deudaTotal)
                .deudas(deudas)
                .vencimientos(vencimientoDtos)
                .build();
    }

    private ChequeraSerie toDomain(ChequeraSerieEntity chequera) {
        if (chequera == null) {
            return null;
        }
        return ChequeraSerie.builder()
                .chequeraId(chequera.getChequeraId())
                .facultadId(chequera.getFacultadId())
                .tipoChequeraId(chequera.getTipoChequeraId())
                .chequeraSerieId(chequera.getChequeraSerieId())
                .personaId(chequera.getPersonaId())
                .documentoId(chequera.getDocumentoId())
                .alternativaId(chequera.getAlternativaId())
                .lectivoId(chequera.getLectivoId())
                .build();
    }
}
