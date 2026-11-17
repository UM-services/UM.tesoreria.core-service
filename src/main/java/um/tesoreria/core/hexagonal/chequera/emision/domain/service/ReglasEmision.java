package um.tesoreria.core.hexagonal.chequera.emision.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Constantes y cálculos de la emisión individual de chequeras.
 *
 * <p>Todas las constantes provienen de {@code frmImpChequera.frm} y
 * {@code clsCtlChequera.cls} de tesoreria.vb6. Se documentan acá, en código, para que
 * la regla sea explícita y testeable (decisión Q3: no usar properties).</p>
 */
public final class ReglasEmision {

    private ReglasEmision() {
    }

    /** Plazo usado por el botón "Aplicar P.A." del legacy. */
    public static final int PLAZO_POLITICA_DIAS = 10;

    /** El legacy propone el 2do vencimiento 10 días después del 1ro. */
    public static final int DIAS_SEGUNDO_VENCIMIENTO = 10;

    /** El legacy propone el 3er vencimiento 15 días después del 2do. */
    public static final int DIAS_TERCER_VENCIMIENTO = 15;

    /**
     * Corte temporal del legacy ({@code CLng(anho) * 100 + mes > 202108}): a partir de
     * septiembre/2021 la cuota usa el arancel elegido; antes usa el arancel de la serie.
     */
    public static final int CORTE_ARANCEL_POR_CUOTA = 202108;

    /** {@code cboArancelTipo}: HPUM se preselecciona con este id. */
    public static final int ARANCEL_TIPO_HPUM_ID = 9;

    /** {@code cboLectivo} deshabilita los lectivos mayores o iguales a este valor. */
    public static final int LECTIVO_MAXIMO_SELECCIONABLE = 37;

    /** {@code makeChequera}: tipo de impresión Rapipago. */
    public static final int TIPO_IMPRESION_RAPIPAGO = 2;

    /** Producto 2 (matrícula) no admite beca. */
    public static final int PRODUCTO_SIN_BECA_ID = 2;

    /** Retención de borradores ABIERTO sin actividad. */
    public static final int DIAS_RETENCION_BORRADOR = 30;

    /** El legacy borra el mail institucional al enviar para esta facultad. */
    public static final int FACULTAD_SIN_EMAIL_INSTITUCIONAL_ID = 15;

    // Códigos de error/aviso del contrato REST
    public static final String SIN_DATOS = "SIN_DATOS";
    public static final String SIN_CURSO = "SIN_CURSO";
    public static final String SIN_ALTERNATIVA = "SIN_ALTERNATIVA";
    public static final String SIN_CUOTAS = "SIN_CUOTAS";
    public static final String SIN_EMAIL_PERSONAL = "SIN_EMAIL_PERSONAL";
    public static final String EMAIL_INVALIDO = "EMAIL_INVALIDO";
    public static final String DEUDA_EXCEDIDA = "DEUDA_EXCEDIDA";
    public static final String SIN_PERMISO_IMPRESION = "SIN_PERMISO_IMPRESION";
    public static final String SIN_PERMISO_MODIFICACION = "SIN_PERMISO_MODIFICACION";
    public static final String SIN_PERMISO_OVERRIDE = "SIN_PERMISO_OVERRIDE";
    public static final String MOTIVO_OVERRIDE_REQUERIDO = "MOTIVO_OVERRIDE_REQUERIDO";
    public static final String CUOTA_INCONSISTENTE = "CUOTA_INCONSISTENTE";
    public static final String CUOTA_INMUTABLE = "CUOTA_INMUTABLE";
    public static final String BORRADOR_DESACTUALIZADO = "BORRADOR_DESACTUALIZADO";
    public static final String BORRADOR_NO_ABIERTO = "BORRADOR_NO_ABIERTO";
    public static final String CHEQUERA_PAGADA = "CHEQUERA_PAGADA";
    public static final String CHEQUERA_DE_BAJA = "CHEQUERA_DE_BAJA";
    public static final String CONFLICTO_NUMERACION = "CONFLICTO_NUMERACION";
    public static final String CLAVE_IDEMPOTENCIA_USADA = "CLAVE_IDEMPOTENCIA_USADA";
    public static final String SIN_ARANCEL_PORCENTAJE = "SIN_ARANCEL_PORCENTAJE";
    public static final String SIN_CURSO_RESUELTO = "SIN_CURSO_RESUELTO";

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    /**
     * Redondeo de importes: {@code HALF_UP} a 2 decimales.
     *
     * <p>Diferencia deliberada con el legacy, que usaba {@code CLng} y por lo tanto
     * truncaba los centavos (decisión Q6).</p>
     */
    public static BigDecimal redondear2(BigDecimal importe) {
        if (importe == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return importe.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Aplica un porcentaje de arancel: {@code importe * porcentaje / 100}.
     */
    public static BigDecimal aplicarPorcentaje(BigDecimal importe, BigDecimal porcentaje) {
        if (importe == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal pct = porcentaje == null ? CIEN : porcentaje;
        return redondear2(importe.multiply(pct).divide(CIEN, 6, RoundingMode.HALF_UP));
    }

    /**
     * Importe del borrador: {@code original * (1 - beca) * porcentajeArancel / 100}.
     *
     * <p>{@code beca} es una fracción (0.20 = 20%), igual que
     * {@code chequera_serie.beca_porcentaje}.</p>
     */
    public static BigDecimal aplicarBecaYArancel(BigDecimal importeOriginal, BigDecimal beca, BigDecimal porcentajeArancel) {
        if (importeOriginal == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal fraccionBeca = beca == null ? BigDecimal.ZERO : beca;
        if (fraccionBeca.compareTo(BigDecimal.ZERO) < 0) {
            fraccionBeca = BigDecimal.ZERO;
        }
        BigDecimal neto = importeOriginal.multiply(BigDecimal.ONE.subtract(fraccionBeca));
        return aplicarPorcentaje(neto, porcentajeArancel);
    }

    /**
     * Multiplicador del descuento por pago en término: {@code 1 - puntos / 100}.
     */
    public static BigDecimal multiplicadorDescuentoPagoTermino(Integer puntos) {
        if (puntos == null || puntos == 0) {
            return BigDecimal.ONE;
        }
        return BigDecimal.ONE.subtract(BigDecimal.valueOf(puntos).divide(CIEN, 6, RoundingMode.HALF_UP));
    }

    /**
     * Clave de período usada por el corte temporal del legacy.
     */
    public static int periodo(Integer anio, Integer mes) {
        if (anio == null || mes == null) {
            return 0;
        }
        return anio * 100 + mes;
    }

    /**
     * Beca aplicable a un producto: el producto 2 (matrícula) nunca lleva beca.
     */
    public static BigDecimal becaAplicable(BigDecimal becaPorcentaje, Integer productoId) {
        if (productoId != null && productoId == PRODUCTO_SIN_BECA_ID) {
            return BigDecimal.ZERO;
        }
        return becaPorcentaje == null ? BigDecimal.ZERO : becaPorcentaje;
    }

    /**
     * Validación heredada de {@code clsDAOCheqCuota.validate}: importes y vencimientos
     * no pueden retroceder.
     */
    public static boolean cuotaConsistente(BigDecimal importe1, BigDecimal importe2, BigDecimal importe3,
                                           java.time.OffsetDateTime vencimiento1,
                                           java.time.OffsetDateTime vencimiento2,
                                           java.time.OffsetDateTime vencimiento3) {
        if (importe1 == null || importe2 == null || importe3 == null
                || vencimiento1 == null || vencimiento2 == null || vencimiento3 == null) {
            return false;
        }
        return importe2.compareTo(importe1) >= 0
                && importe3.compareTo(importe2) >= 0
                && !vencimiento2.isBefore(vencimiento1)
                && !vencimiento3.isBefore(vencimiento2);
    }

    public static boolean emailValido(String email) {
        return email != null && email.trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }
}
