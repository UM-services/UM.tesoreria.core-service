package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RetencionPolicyTest {

    private static DeudaEnvio deuda(Integer lectivoId, Integer cuotas) {
        return DeudaEnvio.builder()
                .deudas(List.of(DeudaEnvio.DeudaChequeraEnvio.builder()
                        .lectivoId(lectivoId)
                        .cuotas(cuotas)
                        .build()))
                .build();
    }

    @Test
    void retieneConMasDeTresCuotasDelLectivo() {
        assertThat(RetencionPolicy.retener(deuda(37, 4), 37, 3)).isTrue();
    }

    @Test
    void noRetieneConTresCuotasExactas() {
        assertThat(RetencionPolicy.retener(deuda(37, 3), 37, 3)).isFalse();
    }

    @Test
    void noRetieneLaDeudaDeOtroLectivo() {
        assertThat(RetencionPolicy.retener(deuda(36, 9), 37, 3)).isFalse();
    }

    @Test
    void noRetieneSinDeudas() {
        assertThat(RetencionPolicy.retener(DeudaEnvio.builder().deudas(List.of()).build(), 37, 3)).isFalse();
        assertThat(RetencionPolicy.retener(null, 37, 3)).isFalse();
    }

    @Test
    void respetaElUmbralConfigurado() {
        assertThat(RetencionPolicy.retener(deuda(37, 5), 37, 5)).isFalse();
        assertThat(RetencionPolicy.retener(deuda(37, 6), 37, 5)).isTrue();
    }

    @Test
    void informaLaCantidadDeCuotasDelLectivo() {
        assertThat(RetencionPolicy.cuotasDelLectivo(deuda(37, 4), 37)).isEqualTo(4);
        assertThat(RetencionPolicy.cuotasDelLectivo(deuda(36, 4), 37)).isNull();
    }
}
