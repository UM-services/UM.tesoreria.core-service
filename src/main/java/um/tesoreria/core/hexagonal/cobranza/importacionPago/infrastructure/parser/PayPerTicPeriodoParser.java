package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.parser;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.CanalImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.LineaImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.RendicionPayPerTic;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ParserArchivoPago;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.PayPerTicRendicionRepository;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Parser del período de PayPerTIC.
 *
 * <p>No hay archivo de rendición: el XLSX se sube antes y llena {@code paypertic}. Este parser
 * toma las filas todavía no importadas del período y las traduce a líneas con las claves que
 * vienen en {@code external_transaction_id}: 1-2 facultad, 3-5 tipo de chequera, 6-10 serie,
 * 11 producto y 14-16 cuota.</p>
 */
@Component
@RequiredArgsConstructor
public class PayPerTicPeriodoParser implements ParserArchivoPago {

    private static final int TIPO_PAGO = 8;

    private final PayPerTicRendicionRepository rendicionRepository;

    @Override
    public CanalImportacion canal() {
        return CanalImportacion.PAY_PER_TIC;
    }

    @Override
    public boolean soportaPeriodo() {
        return true;
    }

    @Override
    public List<LineaImportacion> parsear(String filename, byte[] contenido) {
        throw new UnsupportedOperationException("PayPerTIC se importa por período, no por archivo");
    }

    @Override
    public List<LineaImportacion> porPeriodo(OffsetDateTime desde, OffsetDateTime hasta) {
        List<LineaImportacion> lineas = new ArrayList<>();
        int numero = 0;
        for (RendicionPayPerTic rendicion : rendicionRepository.findPendientesByPeriodo(desde, hasta)) {
            numero++;
            LineaImportacion linea = toLinea(numero, rendicion);
            if (linea != null) {
                lineas.add(linea);
            }
        }
        return lineas;
    }

    private LineaImportacion toLinea(int numero, RendicionPayPerTic rendicion) {
        String id = rendicion.externalTransactionId();
        if (id == null || id.length() < 16) {
            return null;
        }
        try {
            return LineaImportacion.builder()
                    .numeroLinea(numero)
                    .referencia(id.trim())
                    .payperticId(rendicion.payperticId())
                    .facultadId(Integer.valueOf(id.substring(0, 2).trim()))
                    .tipoChequeraId(Integer.valueOf(id.substring(2, 5).trim()))
                    .chequeraSerieId(Long.valueOf(id.substring(5, 10).trim()))
                    .productoId(Integer.valueOf(id.substring(10, 11).trim()))
                    .cuotaId(Integer.valueOf(id.substring(13, 16).trim()))
                    .importe(rendicion.amount())
                    .fechaPago(rendicion.paymentDate())
                    .fechaAcreditacion(rendicion.accreditationDate())
                    .tipoPagoId(TIPO_PAGO)
                    .build();
        } catch (RuntimeException e) {
            return null;
        }
    }

}
