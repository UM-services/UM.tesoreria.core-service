package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model;

/**
 * Período de cursada de una materia. El orden de las constantes es el orden de presentación:
 * anual, primer semestre, segundo semestre y, al final, sin período.
 */
public enum PeriodoCursada {
    ANUAL,
    PRIMER_SEMESTRE,
    SEGUNDO_SEMESTRE,
    SIN_PERIODO;

    /**
     * Traduce el código numérico de la facultad: {@code 0} anual, {@code 1} primer semestre,
     * {@code 2} segundo semestre. Cualquier otro valor, o {@code null}, es {@link #SIN_PERIODO}.
     */
    public static PeriodoCursada desde(Integer codigo) {
        if (codigo == null) {
            return SIN_PERIODO;
        }
        return switch (codigo) {
            case 0 -> ANUAL;
            case 1 -> PRIMER_SEMESTRE;
            case 2 -> SEGUNDO_SEMESTRE;
            default -> SIN_PERIODO;
        };
    }
}
