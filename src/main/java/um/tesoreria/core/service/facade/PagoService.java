package um.tesoreria.core.service.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.ChequeraPagoException;
import um.tesoreria.core.kotlin.model.ChequeraCuota;
import um.tesoreria.core.kotlin.model.ChequeraPago;
import um.tesoreria.core.kotlin.model.ChequeraPagoAsiento;
import um.tesoreria.core.model.ChequeraPagoReemplazo;
import um.tesoreria.core.model.ChequeraTotal;
import um.tesoreria.core.model.MercadoPagoContext;
import um.tesoreria.core.model.dto.ItemAsientoDto;
import um.tesoreria.core.model.dto.PagoDto;
import um.tesoreria.core.service.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PagoService {

    private static final int TIPO_MERCADO_PAGO = 18;

    private final ChequeraPagoService chequeraPagoService;
    private final ChequeraPagoReemplazoService chequeraPagoReemplazoService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final ChequeraTotalService chequeraTotalService;
    private final MercadoPagoContextService mercadoPagoContextService;
    private final ChequeraPagoAsientoService chequeraPagoAsientoService;

    public PagoService(ChequeraPagoService chequeraPagoService,
                       ChequeraPagoReemplazoService chequeraPagoReemplazoService,
                       ChequeraCuotaService chequeraCuotaService,
                       ChequeraTotalService chequeraTotalService,
                       MercadoPagoContextService mercadoPagoContextService, ChequeraPagoAsientoService chequeraPagoAsientoService) {
        this.chequeraPagoService = chequeraPagoService;
        this.chequeraPagoReemplazoService = chequeraPagoReemplazoService;
        this.chequeraCuotaService = chequeraCuotaService;
        this.chequeraTotalService = chequeraTotalService;
        this.mercadoPagoContextService = mercadoPagoContextService;
        this.chequeraPagoAsientoService = chequeraPagoAsientoService;
    }

    public List<PagoDto> getPagos(Integer tipoPagoId, OffsetDateTime fecha) {
        List<ChequeraPago> pagos = (tipoPagoId != TIPO_MERCADO_PAGO) 
            ? chequeraPagoService.findAllByTipoPagoIdAndFechaAcreditacion(tipoPagoId, fecha)
            : chequeraPagoService.findAllByTipoPagoIdAndFechaPago(tipoPagoId, fecha);
        logChequeraPagos(pagos);

        var pagosUnificados = new ArrayList<>(pagos.stream()
                .map(pago -> PagoDto.builder()
                        .facultadId(pago.getFacultadId())
                        .tipoChequeraId(pago.getTipoChequeraId())
                        .chequeraSerieId(pago.getChequeraSerieId())
                        .productoId(pago.getProductoId())
                        .alternativaId(pago.getAlternativaId())
                        .cuotaId(pago.getCuotaId())
                        .orden(pago.getOrden())
                        .fecha(tipoPagoId != TIPO_MERCADO_PAGO ? pago.getAcreditacion() : pago.getFecha())
                        .importePagado(pago.getImporte())
                        .reemplazo((byte) 0)
                        .chequeraPagoId(pago.getChequeraPagoId())
                        .chequeraPagoReemplazoId(null)
                        .tipoPagoId(pago.getTipoPagoId())
                        .build())
                .toList());

        // Obtener y agregar los reemplazos
        List<ChequeraPagoReemplazo> reemplazos = chequeraPagoReemplazoService.findAllByTipoPagoIdAndFechaAcreditacion(tipoPagoId, fecha);
        logChequeraPagoReemplazos(reemplazos);
        
        pagosUnificados.addAll(reemplazos.stream()
                .map(reemplazo -> PagoDto.builder()
                        .facultadId(reemplazo.getFacultadId())
                        .tipoChequeraId(reemplazo.getTipoChequeraId())
                        .chequeraSerieId(reemplazo.getChequeraSerieId())
                        .productoId(reemplazo.getProductoId())
                        .alternativaId(reemplazo.getAlternativaId())
                        .cuotaId(reemplazo.getCuotaId())
                        .orden(reemplazo.getOrden())
                        .fecha(reemplazo.getAcreditacion())
                        .importePagado(reemplazo.getImporte())
                        .reemplazo((byte) 1)
                        .chequeraPagoId(null)
                        .chequeraPagoReemplazoId(reemplazo.getChequeraPagoReemplazoId())
                        .tipoPagoId(reemplazo.getTipoPagoId())
                        .build())
                .toList());

        return pagosUnificados;
    }

    public List<ItemAsientoDto> getItems(Integer tipoPagoId, OffsetDateTime fecha) {
        // Obtener asientos de pagos normales y reemplazos
        List<ChequeraPagoAsiento> asientosPagos = chequeraPagoAsientoService.findAllByTipoPagoIdAndFecha(tipoPagoId, fecha);
        logChequeraPagoAsientos(asientosPagos);
        
        // Agrupar y sumar importes por fecha, cuenta y debita
        return asientosPagos.stream()
            .collect(Collectors.groupingBy(
                asiento -> new AsientoKey(
                    asiento.getFecha(),
                    asiento.getCuenta(),
                    asiento.getDebita()
                ),
                Collectors.reducing(
                    BigDecimal.ZERO,
                    ChequeraPagoAsiento::getImporte,
                    BigDecimal::add
                )
            ))
            .entrySet()
            .stream()
            .map(entry -> ItemAsientoDto.builder()
                .fecha(entry.getKey().fecha)
                .numeroCuenta(entry.getKey().cuenta)
                .debita(entry.getKey().debita)
                .importe(entry.getValue())
                .build())
            .sorted(Comparator
                .comparing(ItemAsientoDto::getFecha)
                .thenComparing(ItemAsientoDto::getNumeroCuenta)
                .thenComparing(ItemAsientoDto::getDebita))
            .collect(Collectors.toList());
    }

    // Clase auxiliar para agrupar por múltiples campos
    private static class AsientoKey {
        private final OffsetDateTime fecha;
        private final BigDecimal cuenta;
        private final Byte debita;

        public AsientoKey(OffsetDateTime fecha, BigDecimal cuenta, Byte debita) {
            this.fecha = fecha;
            this.cuenta = cuenta;
            this.debita = debita;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AsientoKey that = (AsientoKey) o;
            return Objects.equals(fecha, that.fecha) &&
                   Objects.equals(cuenta, that.cuenta) &&
                   Objects.equals(debita, that.debita);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fecha, cuenta, debita);
        }
    }

    public ChequeraPago registraPagoMP(Long mercadoPagoContextId) {
        log.debug("Processing registraPagoMP");

        final Integer MERCADOPAGO = 18;

        var mercadoPagoContext = mercadoPagoContextService.findByMercadoPagoContextId(mercadoPagoContextId);
        logMercadoPagoContext(mercadoPagoContext);

        var chequeraCuota = chequeraCuotaService.findByChequeraCuotaId(mercadoPagoContext.getChequeraCuotaId());
        logChequeraCuota(chequeraCuota);

        // Evita registrar pagos repetidos para el caso de una nueva notificacion de MP
        try {
            var chequeraPago = chequeraPagoService.findByIdMercadoPago(mercadoPagoContext.getIdMercadoPago());
            logChequeraPago(chequeraPago);
            return chequeraPago;
        } catch (ChequeraPagoException e) {
            log.debug("No Existe Pago de MP previo");
        }

        var nextOrder = chequeraPagoService.nextOrden(chequeraCuota.getFacultadId(), chequeraCuota.getTipoChequeraId(), chequeraCuota.getChequeraSerieId(), chequeraCuota.getProductoId(), chequeraCuota.getAlternativaId(), chequeraCuota.getCuotaId());
        log.debug("NextOrder = {}", nextOrder);

        var fechaAcreditacion = LocalDate.now().isAfter(LocalDate.of(2025, 9, 1).minusDays(1)) ? mercadoPagoContext.getFechaPago() : mercadoPagoContext.getFechaAcreditacion();

        var chequeraPago = new ChequeraPago.Builder()
                .chequeraCuotaId(chequeraCuota.getChequeraCuotaId())
                .facultadId(chequeraCuota.getFacultadId())
                .tipoChequeraId(chequeraCuota.getTipoChequeraId())
                .chequeraSerieId(chequeraCuota.getChequeraSerieId())
                .productoId(chequeraCuota.getProductoId())
                .alternativaId(chequeraCuota.getAlternativaId())
                .cuotaId(chequeraCuota.getCuotaId())
                .orden(nextOrder)
                .mes(chequeraCuota.getMes())
                .anho(chequeraCuota.getAnho())
                .fecha(mercadoPagoContext.getFechaPago())
                .acreditacion(fechaAcreditacion)
                .importe(mercadoPagoContext.getImportePagado())
                .tipoPagoId(MERCADOPAGO)
                .idMercadoPago(mercadoPagoContext.getIdMercadoPago())
                .archivo("MercadoPago")
                .build();
        logChequeraPago(chequeraPago);
        chequeraPago = chequeraPagoService.add(chequeraPago, chequeraCuotaService);
        log.debug("After");
        logChequeraPago(chequeraPago);

        marcarPago(chequeraCuota.getFacultadId(), chequeraCuota.getTipoChequeraId(), chequeraCuota.getChequeraSerieId(), chequeraCuota.getProductoId(), chequeraCuota.getAlternativaId(), chequeraCuota.getCuotaId(), chequeraCuotaService);

        return chequeraPago;
    }

    public void marcarPago(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId, ChequeraCuotaService chequeraCuotaService) {
        log.debug("Processing marcarPago");

        boolean pagado = chequeraPagoService.isPagado(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId, chequeraCuotaService);

        var chequeraCuota = chequeraCuotaService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
        chequeraCuota.setPagado((byte) (pagado ? 1 : 0));
        chequeraCuota = chequeraCuotaService.update(chequeraCuota, chequeraCuota.getChequeraCuotaId());
        logChequeraCuota(chequeraCuota);

        calcularPagado(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId);
    }

    private void calcularPagado(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId) {
        log.debug("Processing calcularPagado");
        var chequeraTotal = chequeraTotalService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId);
        chequeraTotal.setTotal(chequeraCuotaService.calculateTotalCuotasActivas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId));
        chequeraTotal.setPagado(chequeraCuotaService.calculateTotalCuotasPagadas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId));
        chequeraTotal = chequeraTotalService.update(chequeraTotal, chequeraTotal.getChequeraTotalId());
        logChequeraTotal(chequeraTotal);
    }

    private void logChequeraTotal(ChequeraTotal chequeraTotal) {
        log.debug("Processing logChequeraTotal");
        try {
            log.debug("ChequeraTotal -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(chequeraTotal));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraTotal jsonify error -> {}", e.getMessage());
        }
    }

    private void logMercadoPagoContext(MercadoPagoContext mercadoPagoContext) {
        log.debug("Processing logMercadoPagoContext");
        try {
            log.debug("MercadoPagoContext -> {}", JsonMapper.builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(mercadoPagoContext));
        } catch (JsonProcessingException e) {
            log.error("MercadoPagoContext error {}", e.getMessage());
        }
    }

    private void logChequeraCuota(ChequeraCuota chequeraCuota) {
        log.debug("Processing logChequeraCuota");
        try {
            log.debug("ChequeraCuota -> {}", JsonMapper.builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraCuota));
        } catch (JsonProcessingException e) {
            log.error("ChequeraCuota error {}", e.getMessage());
        }
    }

    private void logChequeraPago(ChequeraPago chequeraPago) {
        log.debug("Processing logChequeraPago");
        try {
            log.debug("ChequeraPago -> {}", JsonMapper.builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraPago));
        } catch (JsonProcessingException e) {
            log.error("ChequeraPago error {}", e.getMessage());
        }
    }

    private void logChequeraPagos(List<ChequeraPago> pagos) {
        log.debug("Processing logChequeraPagos");
        try {
            log.debug("ChequeraPagos -> {}", JsonMapper.builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(pagos));
        } catch (JsonProcessingException e) {
            log.error("ChequeraPagos error {}", e.getMessage());
        }
    }

    private void logChequeraPagoReemplazos(List<ChequeraPagoReemplazo> reemplazos) {
        log.debug("Processing logChequeraPagoReemplazos");
        try {
            log.debug("ChequeraPagoReemplazos -> {}", JsonMapper.builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(reemplazos));
        } catch (JsonProcessingException e) {
            log.error("ChequeraPagoReemplazos error {}", e.getMessage());
        }
    }

    private void logChequeraPagoAsientos(List<ChequeraPagoAsiento> asientosPagos) {
        log.debug("Processing logChequeraPagoAsientos");
        try {
            log.debug("ChequeraPagoAsientos -> {}", JsonMapper.builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(asientosPagos));
        } catch (JsonProcessingException e) {
            log.error("ChequeraPagoAsientos error {}", e.getMessage());
        }
    }

}
