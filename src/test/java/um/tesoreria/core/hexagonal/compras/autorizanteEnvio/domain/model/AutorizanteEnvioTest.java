package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutorizanteEnvioTest {

    @Test
    void sinDependenciasQuedaVacio() {
        AutorizanteEnvio autorizante = new AutorizanteEnvio(9, null);

        assertThat(autorizante.dependenciaIds()).isEmpty();
        assertThat(autorizante.dependenciaIdsOrdenadas()).isEmpty();
    }

    @Test
    void agregarSumaSinDuplicar() {
        AutorizanteEnvio autorizante = new AutorizanteEnvio(9, Set.of(1));

        AutorizanteEnvio agregado = autorizante.agregar(2).agregar(1);

        assertThat(agregado.dependenciaIds()).containsExactlyInAnyOrder(1, 2);
        assertThat(agregado.dependenciaIdsOrdenadas()).containsExactly(1, 2);
    }

    @Test
    void quitarEliminaSoloLaIndicada() {
        AutorizanteEnvio autorizante = new AutorizanteEnvio(9, Set.of(1, 2));

        AutorizanteEnvio quitado = autorizante.quitar(1);

        assertThat(quitado.dependenciaIds()).containsExactly(2);
    }

    @Test
    void quitarUnaInexistenteNoCambiaElConjunto() {
        AutorizanteEnvio autorizante = new AutorizanteEnvio(9, Set.of(1));

        assertThat(autorizante.quitar(3).dependenciaIds()).containsExactly(1);
    }

    @Test
    void reemplazarDejaExactamenteElConjuntoIndicado() {
        AutorizanteEnvio autorizante = new AutorizanteEnvio(9, Set.of(1, 2));

        AutorizanteEnvio reemplazado = autorizante.reemplazar(List.of(3, 4, 3));

        assertThat(reemplazado.dependenciaIds()).containsExactlyInAnyOrder(3, 4);
    }

    @Test
    void cubreSoloSusDependencias() {
        AutorizanteEnvio autorizante = new AutorizanteEnvio(9, Set.of(1, 2));

        assertThat(autorizante.cubre(1)).isTrue();
        assertThat(autorizante.cubre(3)).isFalse();
        assertThat(autorizante.cubre(null)).isFalse();
    }

    @Test
    void exigeUsuario() {
        assertThatThrownBy(() -> new AutorizanteEnvio(null, Set.of(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
