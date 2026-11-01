package um.tesoreria.core.hexagonal.contable.ejercicio.domain.model;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;

/** Regla de clsCtlEjercicios.add: 1 de marzo al último día de febrero, año siguiente al último. */
public final class EjercicioSiguiente {

    public static final String PREFIJO_NOMBRE = "Ejercicio Económico ";

    private EjercicioSiguiente() {
    }

    public static String nombrePara(LocalDate fechaInicio) {
        return PREFIJO_NOMBRE + fechaInicio.getYear();
    }

    /** Equivale a Year(último.fechaInicio) > Year(hoy) + 1: no se crean más ejercicios. */
    public static boolean superaLimite(Ejercicio ultimo, LocalDate hoy) {
        return ultimo.getFechaInicio().getYear() > hoy.getYear() + 1;
    }

    /** Un ejercicio es futuro si inicia en un año posterior al de hoy (regla de baja de clsCtlEjercicios.delete). */
    public static boolean esFuturo(Ejercicio ejercicio, LocalDate hoy) {
        return ejercicio.getFechaInicio().getYear() > hoy.getYear();
    }

    /** 1 de marzo al último día de febrero siguiente: el único período que el alta sabe continuar. */
    public static boolean esPeriodoRegular(LocalDate inicio, LocalDate fin) {
        return inicio.getMonth() == Month.MARCH && inicio.getDayOfMonth() == 1
                && fin.equals(YearMonth.of(inicio.getYear() + 1, Month.FEBRUARY).atEndOfMonth());
    }

    public static Ejercicio despuesDe(Ejercicio ultimo) {
        int anio = ultimo.getFechaInicio().getYear() + 1;
        LocalDate inicio = LocalDate.of(anio, Month.MARCH, 1);
        return Ejercicio.builder()
                .ejercicioId(ultimo.getEjercicioId() + 1)
                .nombre(nombrePara(inicio))
                .fechaInicio(inicio)
                .fechaFinal(YearMonth.of(anio + 1, Month.FEBRUARY).atEndOfMonth())
                .bloqueado((byte) 0)
                .ordenContableResultado(0)
                .ordenContableBienesUso(0)
                .build();
    }
}
