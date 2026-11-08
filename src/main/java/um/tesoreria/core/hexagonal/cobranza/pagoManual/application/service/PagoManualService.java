package um.tesoreria.core.hexagonal.cobranza.pagoManual.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.application.exception.PagoManualException;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.application.exception.PagoManualNotFoundException;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.PagoManual;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.ResultadoEliminacionPago;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.SolicitudPagoManual;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

/**
 * Alta y baja de pagos manuales.
 *
 * <p>El alta valida la cuota, el saldo y la sede, registra el pago y deja la cuota/totales
 * actualizados en la misma transacción. La baja sólo acepta pagos manuales
 * ({@code tipoPagoId = 2}) y también recalcula la cuota y los totales.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PagoManualService {

    private static final int TIPO_PAGO_MANUAL = 2;

    private final ChequeraPagoService chequeraPagoService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final ChequeraSerieService chequeraSerieService;

    @Transactional
    public PagoManual registrar(SolicitudPagoManual solicitud) {
        validarSolicitud(solicitud);
        ChequeraCuota cuota = buscarCuota(solicitud.chequeraCuotaId());
        if (cuota.getBaja() != null && cuota.getBaja() == 1) {
            throw new PagoManualException("La cuota está de baja", true);
        }

        List<ChequeraPago> pagos = chequeraPagoService.findAllByCuota(cuota.getFacultadId(),
                cuota.getTipoChequeraId(), cuota.getChequeraSerieId(), cuota.getProductoId(),
                cuota.getAlternativaId(), cuota.getCuotaId());
        BigDecimal pagado = pagos.stream().map(ChequeraPago::getImporte)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal importeCuota = cuota.getImporte1() == null ? BigDecimal.ZERO : cuota.getImporte1();
        BigDecimal saldo = importeCuota.subtract(pagado);
        if (saldo.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagoManualException("La cuota ya está pagada", true);
        }
        if (solicitud.importe().compareTo(saldo) > 0) {
            throw new PagoManualException("El importe supera el saldo de la cuota ("
                    + saldo.setScale(2, RoundingMode.HALF_UP) + ")");
        }
        validarSede(cuota, solicitud.geograficaId());

        int orden = chequeraPagoService.nextOrden(cuota.getFacultadId(), cuota.getTipoChequeraId(),
                cuota.getChequeraSerieId(), cuota.getProductoId(), cuota.getAlternativaId(), cuota.getCuotaId());
        ChequeraPago pago = chequeraPagoService.create(ChequeraPago.builder()
                .chequeraCuotaId(cuota.getChequeraCuotaId())
                .facultadId(cuota.getFacultadId())
                .tipoChequeraId(cuota.getTipoChequeraId())
                .chequeraSerieId(cuota.getChequeraSerieId())
                .productoId(cuota.getProductoId())
                .alternativaId(cuota.getAlternativaId())
                .cuotaId(cuota.getCuotaId())
                .orden(orden)
                .mes(cuota.getMes())
                .anho(cuota.getAnho())
                .fecha(solicitud.fecha())
                .importe(solicitud.importe().setScale(2, RoundingMode.HALF_UP))
                .path("")
                .archivo("")
                .observaciones(Objects.toString(solicitud.observaciones(), ""))
                .verificador(0)
                .tipoPagoId(TIPO_PAGO_MANUAL)
                .build());
        chequeraPagoService.marcarPago(cuota.getFacultadId(), cuota.getTipoChequeraId(),
                cuota.getChequeraSerieId(), cuota.getProductoId(), cuota.getAlternativaId(),
                cuota.getCuotaId());
        return toDomain(pago);
    }

    @Transactional
    public ResultadoEliminacionPago eliminar(Long chequeraPagoId) {
        ChequeraPago pago = buscarPago(chequeraPagoId);
        if (!Objects.equals(pago.getTipoPagoId(), TIPO_PAGO_MANUAL)) {
            throw new PagoManualException("Sólo se pueden eliminar pagos manuales", true);
        }
        chequeraPagoService.delete(chequeraPagoId);
        chequeraPagoService.marcarPago(pago.getFacultadId(), pago.getTipoChequeraId(),
                pago.getChequeraSerieId(), pago.getProductoId(), pago.getAlternativaId(), pago.getCuotaId());
        return new ResultadoEliminacionPago(chequeraPagoId, true);
    }

    private void validarSolicitud(SolicitudPagoManual solicitud) {
        if (solicitud == null || solicitud.chequeraCuotaId() == null) {
            throw new PagoManualException("La cuota es obligatoria");
        }
        if (solicitud.importe() == null || solicitud.importe().compareTo(BigDecimal.ZERO) <= 0) {
            throw new PagoManualException("El importe debe ser mayor que cero");
        }
        if (solicitud.fecha() == null) {
            throw new PagoManualException("La fecha de pago es obligatoria");
        }
        if (solicitud.fecha().toLocalDate().isAfter(LocalDate.now(ZoneOffset.UTC))) {
            throw new PagoManualException("La fecha de pago no puede ser futura");
        }
    }

    private ChequeraCuota buscarCuota(Long chequeraCuotaId) {
        try {
            return chequeraCuotaService.findByChequeraCuotaId(chequeraCuotaId);
        } catch (RuntimeException e) {
            throw new PagoManualNotFoundException("No existe la cuota " + chequeraCuotaId);
        }
    }

    private ChequeraPago buscarPago(Long chequeraPagoId) {
        try {
            return chequeraPagoService.findByChequeraPagoId(chequeraPagoId);
        } catch (RuntimeException e) {
            throw new PagoManualNotFoundException("No existe el pago " + chequeraPagoId);
        }
    }

    private void validarSede(ChequeraCuota cuota, Integer geograficaId) {
        if (geograficaId == null || geograficaId <= 1) {
            return;
        }
        ChequeraSerie serie = chequeraSerieService.findByUnique(cuota.getFacultadId(),
                cuota.getTipoChequeraId(), cuota.getChequeraSerieId());
        if (!Objects.equals(serie.getGeograficaId(), geograficaId)) {
            throw new PagoManualException("La chequera pertenece a otra sede");
        }
    }

    private PagoManual toDomain(ChequeraPago pago) {
        return new PagoManual(pago.getChequeraPagoId(), pago.getChequeraCuotaId(), pago.getFacultadId(),
                pago.getTipoChequeraId(), pago.getChequeraSerieId(), pago.getProductoId(),
                pago.getAlternativaId(), pago.getCuotaId(), pago.getFecha(), pago.getImporte(),
                pago.getObservaciones(), pago.getTipoPagoId());
    }

}
