package um.tesoreria.core.service.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    private final FacturacionElectronicaService facturacionElectronicaService;
    private final ReciboMessageCheckService reciboMessageCheckService;

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

    public ChequeraPago registraPagoMP(Long mercadoPagoContextId) {
        log.debug("\n\nProcessing PagoService.registraPagoMP\n\n");

        final Integer MERCADOPAGO = 18;

        var mercadoPagoContext = mercadoPagoContextService.findByMercadoPagoContextId(mercadoPagoContextId);
        var chequeraCuota = chequeraCuotaService.findByChequeraCuotaId(mercadoPagoContext.getChequeraCuotaId());

        // Evita registrar pagos repetidos para el caso de una nueva notificacion de MP
        try {
            return chequeraPagoService.findByIdMercadoPago(mercadoPagoContext.getIdMercadoPago());
        } catch (ChequeraPagoException e) {
            log.debug("No Existe Pago de MP previo");
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
                .tipoPagoId(MERCADOPAGO)
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
     * Deshace el pago de una cuota registrado previamente vía MercadoPago,
     * cuando MP informa que ese pago ya no es válido (hoy: "rejected",
     * "refunded" y "charged_back"; ver ESTADOS_REVERSION en
     * ProcessPaymentEventUseCaseImpl).
     * <p>
     * Es el espejo inverso de {@link #registraPagoMP(Long)}: en vez de crear
     * el ChequeraPago y marcar la cuota como pagada, lo busca, borra el
     * asiento contable asociado (si existe) y el ChequeraPago en sí, limpia
     * chequeraPagoId/importePagado/fechaPago/fechaAcreditacion del contexto
     * (dejándolo como si ese pago nunca hubiera existido, más allá del
     * status/idMercadoPago/payment que quedan con los datos del evento que
     * disparó la reversión, para trazabilidad), y vuelve a llamar
     * {@link #marcarPago} para que la cuota y el total de la chequera queden
     * consistentes con eso.
     * <p>
     * Es idempotente a propósito: si no hay ningún ChequeraPago para el
     * idMercadoPago del contexto —porque nunca se llegó a registrar (ej. un
     * "rejected" que llega sin que antes hubo un "approved"), o porque ya se
     * revirtió antes (MP reenvía la misma notificación)— el método no hace
     * nada y corta ahí, sin tocar el contexto, la cuota ni el total.
     * <p>
     * OJO — si el pago ya tiene una o más facturas electrónicas generadas
     * (FacturacionElectronica) o registros de mensaje/recibo enviados
     * (ReciboMessageCheck), no se los toca ni se los borra (ARCA gestiona
     * el aspecto fiscal por su cuenta, y el recibo es solo un log de
     * auditoría) pero SÍ se los desvincula del pago (chequeraPagoId → null
     * en cada uno, y también la referencia de objeto chequeraPago → null
     * en el caso de la factura), para poder borrar el ChequeraPago sin
     * chocar con los FK reales que existen en la base
     * (facturacion_electronica → chequera_pago,
     * recibo_message_check → chequera_pago) ni con el
     * TransientPropertyValueException que tira Hibernate si se borra el
     * ChequeraPago en la misma transacción dejando esa asociación colgando.
     *
     * @param mercadoPagoContextId id del MercadoPagoContext asociado a la
     *                             notificación de MercadoPago que disparó la
     *                             reversión
     */
    public void revertirPagoMP(Long mercadoPagoContextId) {
        log.debug("\n\nProcessing PagoService.revertirPagoMP\n\n");

        var mercadoPagoContext = mercadoPagoContextService.findByMercadoPagoContextId(mercadoPagoContextId);
        var chequeraCuota = chequeraCuotaService.findByChequeraCuotaId(mercadoPagoContext.getChequeraCuotaId());

        ChequeraPago chequeraPago;
        try {
            chequeraPago = chequeraPagoService.findByIdMercadoPago(mercadoPagoContext.getIdMercadoPago());
        } catch (ChequeraPagoException e) {
            // No hay ChequeraPago que revertir: o nunca se llegó a registrar
            // el pago (ej. "rejected" directo), o ya se revirtió antes
            // (notificacion de MP repetida). Idempotente, como registraPagoMP.
            log.debug("No existe ChequeraPago para revertir (idMercadoPago={})", mercadoPagoContext.getIdMercadoPago());
            return;
        }

        // Si el pago ya tiene facturas electrónicas generadas, hay un FK
        // real en la base (facturacion_electronica -> chequera_pago) que
        // impediría borrar el ChequeraPago mientras alguna factura lo siga
        // apuntando. ARCA se encarga del aspecto fiscal por su cuenta, así
        // que acá solo desvinculamos TODAS las facturas del pago (no se
        // borra ninguna factura, quedan como registro histórico) para poder
        // seguir con el borrado normal. Ojo: puede haber más de una factura
        // para el mismo pago (ej. original + nota de crédito), por eso se
        // usa findAllByChequeraPagoIds y no un findByChequeraPagoId único.
        var facturasExistentes = facturacionElectronicaService.findAllByChequeraPagoIds(List.of(chequeraPago.getChequeraPagoId()));
        for (var factura : facturasExistentes) {
            log.info("El pago {} (mercadoPagoContextId={}) ya tiene una factura electrónica generada (facturacionElectronicaId={}). Se desvincula la factura del pago para poder revertirlo; ARCA gestiona el aspecto fiscal por su cuenta.",
                    chequeraPago.getChequeraPagoId(), mercadoPagoContextId, factura.getFacturacionElectronicaId());
            factura.setChequeraPagoId(null);
            // También hay que limpiar la referencia de objeto (no solo el id
            // escalar): si queda apuntando al ChequeraPagoEntity en memoria,
            // Hibernate la trata como una asociación inconsistente al hacer
            // flush una vez que ese ChequeraPago se borra más abajo, en la
            // misma transacción (TransientPropertyValueException).
            factura.setChequeraPago(null);
            facturacionElectronicaService.update(factura, factura.getFacturacionElectronicaId());
        }

        // Mismo problema, otra tabla: recibo_message_check también tiene un
        // FK real contra chequera_pago (es el log del mensaje/recibo enviado
        // al alumno avisándole del pago o la factura). Es un registro de
        // auditoría, no un documento fiscal — igual lo desvinculamos en vez
        // de borrarlo, para no perder ese historial.
        var recibosExistentes = reciboMessageCheckService.findAllByChequeraPagoId(chequeraPago.getChequeraPagoId());
        for (var recibo : recibosExistentes) {
            log.info("El pago {} (mercadoPagoContextId={}) tiene un ReciboMessageCheck asociado ({}). Se desvincula para poder revertir el pago.",
                    chequeraPago.getChequeraPagoId(), mercadoPagoContextId, recibo.getReciboMessageCheckId());
            recibo.setChequeraPagoId(null);
            reciboMessageCheckService.update(recibo);
        }

        // Se borra primero el asiento contable (depende del pago), y recién
        // después el pago en sí — para no dejar nunca un asiento huérfano
        // apuntando a un ChequeraPago que ya no existe.
        chequeraPagoAsientoService.deleteAllByChequeraPagoId(chequeraPago.getChequeraPagoId());
        chequeraPagoService.deleteByChequeraPagoId(chequeraPago.getChequeraPagoId());

        mercadoPagoContext.setChequeraPagoId(null);
        mercadoPagoContext.setImportePagado(BigDecimal.ZERO);
        mercadoPagoContext.setFechaPago(null);
        mercadoPagoContext.setFechaAcreditacion(null);
        mercadoPagoContextService.update(mercadoPagoContext, mercadoPagoContextId);

        marcarPago(chequeraCuota.getFacultadId(), chequeraCuota.getTipoChequeraId(), chequeraCuota.getChequeraSerieId(), chequeraCuota.getProductoId(), chequeraCuota.getAlternativaId(), chequeraCuota.getCuotaId(), chequeraCuotaService);
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