package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import java.util.Set;

/**
 * Canales de importación de rendiciones del menú Pagos.
 *
 * <p>Cada canal conoce los {@code tipoPagoId} (tabla {@code tipopago}) con los que se registran
 * sus pagos. Ese conjunto es lo que permite reconstruir el canal de un archivo sin agregar
 * columnas a {@code archivo_importado}: se cruza {@code archivo_banco.ArB_Archivo} con
 * {@code archivo_banco.ArB_TPa_ID}.</p>
 */
public enum CanalImportacion {

    PMC(Set.of(4, 11, 12), false, null),
    RAPIPAGO(Set.of(3), false, null),
    PAY_PER_TIC(Set.of(8), true, null),
    PAGAR(Set.of(13), false, null),
    CLICK_PAGOS(Set.of(15, 16, 17), false, null),
    DEBITO_DIRECTO(Set.of(5), false, 2),
    DEBITO_VISA(Set.of(6), false, 1);

    private final Set<Integer> tipoPagoIds;
    private final boolean porPeriodo;
    private final Integer debitoTipoId;

    CanalImportacion(Set<Integer> tipoPagoIds, boolean porPeriodo, Integer debitoTipoId) {
        this.tipoPagoIds = tipoPagoIds;
        this.porPeriodo = porPeriodo;
        this.debitoTipoId = debitoTipoId;
    }

    public Set<Integer> getTipoPagoIds() {
        return tipoPagoIds;
    }

    /**
     * {@code true} para los canales que no se importan desde un archivo de rendición sino desde un
     * rango de fechas sobre datos ya subidos (hoy sólo PayPerTIC).
     */
    public boolean isPorPeriodo() {
        return porPeriodo;
    }

    /** {@code 1} VISA, {@code 2} Débito Directo; {@code null} para los canales que no son débitos. */
    public Integer getDebitoTipoId() {
        return debitoTipoId;
    }

    public boolean isDebito() {
        return debitoTipoId != null;
    }

    public static CanalImportacion fromPath(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Canal de importación requerido");
        }
        String normalizado = value.trim().toLowerCase().replace('_', '-');
        return switch (normalizado) {
            case "pmc", "pago-mis-cuentas", "pagomiscuentas" -> PMC;
            case "rapipago" -> RAPIPAGO;
            case "pay-per-tic", "paypertic", "ppt" -> PAY_PER_TIC;
            case "pagar" -> PAGAR;
            case "click-de-pagos", "clickpagos", "click-pagos", "cdp" -> CLICK_PAGOS;
            case "debito-directo", "debito-directo-cbu", "debito" -> DEBITO_DIRECTO;
            case "debito-visa", "visa" -> DEBITO_VISA;
            default -> throw new IllegalArgumentException("Canal de importación desconocido: " + value);
        };
    }

}
