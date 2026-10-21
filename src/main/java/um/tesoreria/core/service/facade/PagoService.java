package um.tesoreria.core.service.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.exception.ChequeraPagoException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.service.MercadoPagoContextService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.persistence.entity.ChequeraPagoEntity;
import um.tesoreria.core.kotlin.model.ChequeraPagoAsiento;
import um.tesoreria.core.model.ChequeraPagoReemplazo;
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
@RequiredArgsConstructor
public class PagoService {

    private static final int TIPO_MERCADO_PAGO = 18;

    private final ChequeraPagoService chequeraPagoService;
    private final ChequeraPagoReemplazoService chequeraPagoReemplazoService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final ChequeraTotalService chequeraTotalService;
    private final MercadoPagoContextService mercadoPagoContextService;
    private final ChequeraPagoAsientoService chequeraPagoAsientoService;

    public List<PagoDto> getPagos(Integer tipoPagoId, OffsetDateTime fecha) {
        List<ChequeraPago> pagos = (tipoPagoId != TIPO_MERCADO_PAGO)
            ? chequeraPagoService.findAllByTipoPagoIdAndFechaAcreditacion(tipoPagoId, fecha)
            : chequeraPagoService.findAllByTipoPagoIdAndFechaPago(tipoPagoId, fecha);

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

    /**
     * Registra el pago de Mercado Pago asociado a un contexto.
     * <p>
     * Idempotente por {@code idMercadoPago}: si el pago ya existe se devuelve el registrado y no se
     * crea una fila nueva. La unicidad la respalda el índice {@code uk_chequera_pago_id_mp}
     * ({@code docs/sql/V415__uk_chequera_pago_id_mp.sql}): si dos entregas concurrentes del mismo
     * evento pasan el pre-check a la vez, la perdedora falla al insertar, la transacción revierte y
     * Kafka reintenta; en el reintento el pre-check encuentra el pago de la ganadora y termina en
     * no-op.
     * <p>
     * Un pago <b>distinto</b> (otro {@code idMercadoPago}) sobre una cuota ya pagada se registra
     * igual, con un {@code WARN}: es un doble pago real y se resuelve por el circuito
     * administrativo (reversa/devolución), no descartándolo acá.
     */
    @Transactional
    public ChequeraPago registraPagoMP(Long mercadoPagoContextId) {
        log.debug("\n\nProcessing PagoService.registraPagoMP\n\n");

        var mercadoPagoContext = mercadoPagoContextService.findByMercadoPagoContextId(mercadoPagoContextId);

        if (mercadoPagoContext.getIdMercadoPago() == null || mercadoPagoContext.getIdMercadoPago().isBlank()) {
            throw new IllegalArgumentException("No se puede registrar un pago de Mercado Pago sin idMercadoPago (contexto "
                    + mercadoPagoContextId + ")");
        }

        var pagoExistente = buscarPagoMercadoPago(mercadoPagoContext.getIdMercadoPago());
        if (pagoExistente != null) {
            log.info("Pago MP {} ya registrado (chequeraPagoId={}): no-op",
                    mercadoPagoContext.getIdMercadoPago(), pagoExistente.getChequeraPagoId());
            return pagoExistente;
        }

        var chequeraCuota = chequeraCuotaService.findByChequeraCuotaId(mercadoPagoContext.getChequeraCuotaId());

        if (Byte.valueOf((byte) 1).equals(chequeraCuota.getPagado())) {
            log.warn("La cuota {} ya estaba pagada y llega un pago de Mercado Pago distinto ({}): se registra un segundo pago (posible doble pago)",
                    chequeraCuota.getChequeraCuotaId(), mercadoPagoContext.getIdMercadoPago());
        }

        var nextOrder = chequeraPagoService.nextOrden(chequeraCuota.getFacultadId(), chequeraCuota.getTipoChequeraId(), chequeraCuota.getChequeraSerieId(), chequeraCuota.getProductoId(), chequeraCuota.getAlternativaId(), chequeraCuota.getCuotaId());
        log.debug("NextOrder = {}", nextOrder);

        var fechaAcreditacion = LocalDate.now().isAfter(LocalDate.of(2025, 9, 1).minusDays(1)) ? mercadoPagoContext.getFechaPago() : mercadoPagoContext.getFechaAcreditacion();

        var chequeraPago = ChequeraPago.builder()
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
                .fecha(mercadoPagoContext.getFechaPago().minusHours(3))
                .acreditacion(fechaAcreditacion.minusHours(3))
                .importe(mercadoPagoContext.getImportePagado())
                .tipoPagoId(TIPO_MERCADO_PAGO)
                .idMercadoPago(mercadoPagoContext.getIdMercadoPago())
                .archivo("MercadoPago")
                .build();
        chequeraPago = chequeraPagoService.create(chequeraPago);

        mercadoPagoContext.setChequeraPagoId(chequeraPago.getChequeraPagoId());
        mercadoPagoContextService.update(mercadoPagoContext, mercadoPagoContextId);

        marcarPago(chequeraCuota.getFacultadId(), chequeraCuota.getTipoChequeraId(), chequeraCuota.getChequeraSerieId(), chequeraCuota.getProductoId(), chequeraCuota.getAlternativaId(), chequeraCuota.getCuotaId(), chequeraCuotaService);

        return chequeraPago;
    }

    /**
     * @return el pago registrado para ese {@code idMercadoPago}, o {@code null} si todavía no existe.
     */
    private ChequeraPago buscarPagoMercadoPago(String idMercadoPago) {
        try {
            return chequeraPagoService.findByIdMercadoPago(idMercadoPago);
        } catch (ChequeraPagoException e) {
            log.debug("No existe pago de Mercado Pago previo para {}", idMercadoPago);
            return null;
        }
    }

    public void marcarPago(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId, ChequeraCuotaService chequeraCuotaService) {
        log.debug("Processing marcarPago");

        boolean pagado = chequeraPagoService.isPagado(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);

        var chequeraCuota = chequeraCuotaService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
        chequeraCuota.setPagado((byte) (pagado ? 1 : 0));
        chequeraCuota = chequeraCuotaService.update(chequeraCuota, chequeraCuota.getChequeraCuotaId());

        calcularPagado(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId);
    }

    private void calcularPagado(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId) {
        log.debug("Processing calcularPagado");
        var chequeraTotal = chequeraTotalService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId);
        chequeraTotal.setTotal(chequeraCuotaService.calculateTotalCuotasActivas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId));
        chequeraTotal.setPagado(chequeraCuotaService.calculateTotalCuotasPagadas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId));
        chequeraTotal = chequeraTotalService.update(chequeraTotal, chequeraTotal.getChequeraTotalId());
    }

}
