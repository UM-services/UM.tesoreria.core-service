package um.tesoreria.core.hexagonal.personas.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.client.tesoreria.mercadopago.PreferenceClient;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.in.GetDeudaPersonaUseCase;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DeudaChequeraDto;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DeudaPersonaDto;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.VencimientoDto;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.service.MercadoPagoProperties;
import um.tesoreria.core.service.facade.MercadoPagoCoreService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class GetDeudaPersonaUseCaseImpl implements GetDeudaPersonaUseCase {

    private final ChequeraSerieService chequeraSerieService;
    private final CalculateDeudaUseCase calculateDeudaUseCase;
    private final ChequeraCuotaService chequeraCuotaService;
    private final MercadoPagoProperties mercadoPagoProperties;
    private final MercadoPagoCoreService mercadoPagoCoreService;

    @Override
    public DeudaPersonaDto deudaByPersona(BigDecimal personaId, Integer documentoId) {
        var deudaCuotas = 0;
        var deudaTotal = BigDecimal.ZERO;
        List<DeudaChequeraDto> deudas = new ArrayList<>();
        for (ChequeraSerie chequera : chequeraSerieService.findAllByPersonaIdAndDocumentoId(personaId, documentoId,
                null)) {
            DeudaChequeraDto deuda = calculateDeudaUseCase.calculateDeuda(chequera);
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
        List<ChequeraSerie> series = chequeraSerieService.findAllByPersonaIdAndDocumentoId(personaId, documentoId, null);

        // 2. Fetch all cuotas using business keys (Facultad, Tipo, Serie) to ensure we get them even if chequeraId is null
        List<ChequeraCuota> allCuotas = new ArrayList<>();
        Map<String, List<ChequeraSerie>> seriesByGroup = series.stream()
                .collect(Collectors.groupingBy(s -> s.getFacultadId() + "-" + s.getTipoChequeraId()));

        for (Map.Entry<String, List<ChequeraSerie>> entry : seriesByGroup.entrySet()) {
            List<ChequeraSerie> groupSeries = entry.getValue();
            if (groupSeries.isEmpty()) continue;
            
            Integer facultadId = groupSeries.getFirst().getFacultadId();
            Integer tipoChequeraId = groupSeries.getFirst().getTipoChequeraId();
            List<Long> serieIds = groupSeries.stream().map(ChequeraSerie::getChequeraSerieId).collect(Collectors.toList());

            allCuotas.addAll(chequeraCuotaService.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIds(
                    facultadId, tipoChequeraId, serieIds));
        }

        // 3. Agrupar por clave de negocio (facultad-tipo-serie). No se toca chequeraId:
        //    la corrección de nulos vive en el job de backfill, no en este GET.
        Map<String, List<ChequeraCuota>> cuotasBySerie = allCuotas.stream()
                .collect(Collectors.groupingBy(c -> c.getFacultadId() + "-" + c.getTipoChequeraId()
                        + "-" + c.getChequeraSerieId()));

        // 4. Process debts
        for (ChequeraSerie chequera : series) {
            DeudaChequeraDto deuda = calculateDeudaUseCase.calculateDeudaExtended(chequera);
            if (deuda.getCuotas() > 0) {
                deudas.add(deuda);
                deudaCuotas += deuda.getCuotas();
                deudaTotal = deudaTotal.add(deuda.getDeuda()).setScale(2, RoundingMode.HALF_UP);
            }

            String serieKey = chequera.getFacultadId() + "-" + chequera.getTipoChequeraId()
                    + "-" + chequera.getChequeraSerieId();
            List<ChequeraCuota> currentCuotas = cuotasBySerie.getOrDefault(serieKey, List.of());

            List<ChequeraCuota> filteredCuotas = currentCuotas.stream()
                    .filter(c -> Objects.equals(c.getAlternativaId(), chequera.getAlternativaId()))
                    .sorted(Comparator.comparing(ChequeraCuota::getVencimiento1, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(ChequeraCuota::getProductoId, Comparator.nullsLast(Comparator.naturalOrder()))
                            .thenComparing(ChequeraCuota::getCuotaId, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();

            for (ChequeraCuota chequeraCuota : filteredCuotas) {
                if (chequeraCuota.getPagado() == 0 && chequeraCuota.getBaja() == 0 && chequeraCuota.getCompensada() == 0
                        && chequeraCuota.getImporte1().compareTo(BigDecimal.ZERO) != 0) {

                    // Con preference-on-deuda=true se mantiene la compatibilidad: el GET crea
                    // (o reutiliza) la preferencia. Con false, la deuda es sólo lectura y el
                    // cliente pide la preferencia por POST /preference/{chequeraCuotaId}.
                    var umPreferenceMPDto = mercadoPagoProperties.isPreferenceOnDeuda()
                            ? mercadoPagoCoreService.makePreference(chequeraCuota.getChequeraCuotaId())
                            : mercadoPagoCoreService.makeContextCuota(chequeraCuota.getChequeraCuotaId());

                    if (umPreferenceMPDto != null) {
                        var context = umPreferenceMPDto.getMercadoPagoContext();

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
