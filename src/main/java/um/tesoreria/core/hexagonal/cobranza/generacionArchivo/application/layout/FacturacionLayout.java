package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout;

import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.CuotaDeuda;

import java.util.Objects;

/**
 * Detalle común de PagoMisCuentas y Click de Pagos. Ambos layouts comparten el registro de
 * detalle y sólo cambian el código de empresa y los anchos de header/trailer.
 */
final class FacturacionLayout {

    private FacturacionLayout() {
    }

    static String detalle(CuotaDeuda cuota) {
        Integer facultadId = nvl(cuota.getFacultadId());
        Integer tipoChequeraId = nvl(cuota.getTipoChequeraId());
        Long chequeraSerieId = cuota.getChequeraSerieId() == null ? 0L : cuota.getChequeraSerieId();
        Integer productoId = nvl(cuota.getProductoId());
        Integer alternativaId = nvl(cuota.getAlternativaId());
        Integer cuotaId = nvl(cuota.getCuotaId());

        String nroReferencia = String.format("%02d%03d%05d", facultadId, tipoChequeraId, chequeraSerieId);
        String idFactura = nroReferencia + String.format("%02d%01d%02d", alternativaId, productoId, cuotaId);
        String mensajeTicket = facultadId + "-" + tipoChequeraId + "-" + chequeraSerieId + "-"
                + alternativaId + "-" + productoId + "-" + cuotaId + " - "
                + String.format("%02d/%d", nvl(cuota.getMes()), nvl(cuota.getAnho()));
        String mensajePantalla = String.format("%02d/%d", nvl(cuota.getMes()), nvl(cuota.getAnho()));
        String codigoBarras = LayoutFormat.left(cuota.getCodigoBarras(), 60);

        return "5"
                + LayoutFormat.left(nroReferencia, 19)
                + LayoutFormat.left(idFactura, 20)
                + "0"
                + LayoutFormat.fecha(cuota.getVencimiento1(), "yyyyMMdd")
                + LayoutFormat.importe(cuota.getImporte1(), 9, 2)
                + LayoutFormat.fecha(cuota.getVencimiento2(), cuota.getVencimiento1(), "yyyyMMdd")
                + LayoutFormat.importe(cuota.getImporte2(), 9, 2)
                + LayoutFormat.fecha(cuota.getVencimiento3(),
                        cuota.getVencimiento2() == null ? cuota.getVencimiento1() : cuota.getVencimiento2(),
                        "yyyyMMdd")
                + LayoutFormat.importe(cuota.getImporte3(), 9, 2)
                + "0".repeat(19)
                + LayoutFormat.left(nroReferencia, 19)
                + LayoutFormat.left(mensajeTicket, 40)
                + LayoutFormat.left(mensajePantalla, 15)
                + codigoBarras
                + "0".repeat(29);
    }

    private static Integer nvl(Integer value) {
        return Objects.requireNonNullElse(value, 0);
    }

}
