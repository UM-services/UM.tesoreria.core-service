package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model;

/**
 * Canales de generación de archivos del menú Pagos.
 */
public enum CanalGeneracion {

    PMC("pmc"),
    CLICK_PAGOS("click-de-pagos"),
    DEBITO_DIRECTO("debito-directo"),
    DEBITO_VISA("debito-visa"),
    PAGAR("pagar"),
    PAY_PER_TIC("pay-per-tic");

    private final String slug;

    CanalGeneracion(String slug) {
        this.slug = slug;
    }

    public String getSlug() {
        return slug;
    }

    public boolean isDebito() {
        return this == DEBITO_DIRECTO || this == DEBITO_VISA;
    }

    public static CanalGeneracion fromPath(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Canal de generación requerido");
        }
        String normalizado = value.trim().toLowerCase().replace('_', '-');
        return switch (normalizado) {
            case "pmc", "pago-mis-cuentas" -> PMC;
            case "click-de-pagos", "clickpagos", "click-pagos", "cdp" -> CLICK_PAGOS;
            case "debito-directo", "debito" -> DEBITO_DIRECTO;
            case "debito-visa", "visa" -> DEBITO_VISA;
            case "pagar" -> PAGAR;
            case "pay-per-tic", "paypertic", "ppt" -> PAY_PER_TIC;
            default -> throw new IllegalArgumentException("Canal de generación desconocido: " + value);
        };
    }

}
