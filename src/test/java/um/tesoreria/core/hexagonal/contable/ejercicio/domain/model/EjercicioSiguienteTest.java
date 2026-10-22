package um.tesoreria.core.hexagonal.contable.ejercicio.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class EjercicioSiguienteTest {

    static Ejercicio ejercicio(int id, String inicio, String fin) {
        return Ejercicio.builder().ejercicioId(id).fechaInicio(LocalDate.parse(inicio)).fechaFinal(LocalDate.parse(fin)).build();
    }

    @Test
    void derivaIdFechasYNombreDelEjercicioDeLaIssue() {
        Ejercicio siguiente = EjercicioSiguiente.despuesDe(ejercicio(15, "2026-03-01", "2027-02-28"));
        assertThat(siguiente.getEjercicioId()).isEqualTo(16);
        assertThat(siguiente.getFechaInicio()).isEqualTo(LocalDate.parse("2027-03-01"));
        assertThat(siguiente.getFechaFinal()).isEqualTo(LocalDate.parse("2028-02-29"));
        assertThat(siguiente.getNombre()).isEqualTo("Ejercicio Económico 2027");
        assertThat(siguiente.getBloqueado()).isZero();
        assertThat(siguiente.getOrdenContableResultado()).isZero();
        assertThat(siguiente.getOrdenContableBienesUso()).isZero();
    }

    @Test
    void febreroBisiestoSoloCuandoElAnioDeCierreLoEs() {
        assertThat(EjercicioSiguiente.despuesDe(ejercicio(16, "2027-03-01", "2028-02-29")).getFechaFinal())
                .isEqualTo(LocalDate.parse("2029-02-28"));
        assertThat(EjercicioSiguiente.despuesDe(ejercicio(14, "2025-03-01", "2026-02-28")).getFechaFinal())
                .isEqualTo(LocalDate.parse("2027-02-28"));
        assertThat(EjercicioSiguiente.despuesDe(ejercicio(13, "2023-03-01", "2024-02-29")).getFechaInicio())
                .isEqualTo(LocalDate.parse("2024-03-01"));
    }

    @Test
    void limiteCompaniaAnioDeInicioDelUltimoConAnioDeHoyMasUno() {
        Ejercicio ultimo2027 = ejercicio(16, "2027-03-01", "2028-02-29");
        Ejercicio ultimo2028 = ejercicio(17, "2028-03-01", "2029-02-28");
        LocalDate hoy2026 = LocalDate.parse("2026-10-21");
        assertThat(EjercicioSiguiente.superaLimite(ejercicio(15, "2026-03-01", "2027-02-28"), hoy2026)).isFalse();
        assertThat(EjercicioSiguiente.superaLimite(ultimo2027, hoy2026)).isFalse();
        assertThat(EjercicioSiguiente.superaLimite(ultimo2028, hoy2026)).isTrue();
        assertThat(EjercicioSiguiente.superaLimite(ultimo2028, LocalDate.parse("2027-01-01"))).isFalse();
    }

    @Test
    void unOffsetDistintoDeUtcSeConvierteAlDiaUtcYNullDevuelveNull() {
        assertThat(EjercicioFechas.aDia(OffsetDateTime.parse("2027-02-28T22:00:00-03:00"))).isEqualTo(LocalDate.parse("2027-03-01"));
        assertThat(EjercicioFechas.aDia(null)).isNull();
        assertThat(EjercicioFechas.aInicioDeDia(null)).isNull();
        assertThat(EjercicioFechas.aFinDeDia(null)).isNull();
    }

    @Test
    void periodoRegularYFuturo() {
        assertThat(EjercicioSiguiente.esPeriodoRegular(LocalDate.parse("2027-03-01"), LocalDate.parse("2028-02-29"))).isTrue();
        assertThat(EjercicioSiguiente.esPeriodoRegular(LocalDate.parse("2027-03-01"), LocalDate.parse("2028-02-28"))).isFalse();
        assertThat(EjercicioSiguiente.esPeriodoRegular(LocalDate.parse("2027-01-01"), LocalDate.parse("2027-12-31"))).isFalse();
        var e = ejercicio(16, "2027-03-01", "2028-02-29");
        assertThat(EjercicioSiguiente.esFuturo(e, LocalDate.parse("2026-12-31"))).isTrue();
        assertThat(EjercicioSiguiente.esFuturo(e, LocalDate.parse("2027-01-01"))).isFalse();
    }

    @Test
    void fechasGuardadasSeLeenComoDiaEnUtcNoEnBuenosAires() {
        OffsetDateTime guardada = OffsetDateTime.parse("2027-03-01T00:00:00Z");
        assertThat(EjercicioFechas.aDia(guardada)).isEqualTo(LocalDate.parse("2027-03-01"));
        // Convertida a Buenos Aires correría un día: es el error que la regla evita.
        assertThat(guardada.atZoneSameInstant(ZoneId.of("America/Argentina/Buenos_Aires")).toLocalDate())
                .isEqualTo(LocalDate.parse("2027-02-28"));
        assertThat(EjercicioFechas.aDia(OffsetDateTime.parse("2028-02-29T00:00:00Z"))).isEqualTo(LocalDate.parse("2028-02-29"));
        assertThat(EjercicioFechas.aInicioDeDia(LocalDate.parse("2028-02-29")))
                .isEqualTo(OffsetDateTime.of(2028, 2, 29, 0, 0, 0, 0, ZoneOffset.UTC));
        assertThat(EjercicioFechas.aFinDeDia(LocalDate.parse("2027-02-28")))
                .isEqualTo(OffsetDateTime.of(2027, 2, 28, 23, 59, 59, 0, ZoneOffset.UTC));
    }
}
