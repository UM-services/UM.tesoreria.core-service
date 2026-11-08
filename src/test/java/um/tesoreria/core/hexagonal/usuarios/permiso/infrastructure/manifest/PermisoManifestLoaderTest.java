package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.manifest;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoManifestException;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PermisoManifestLoaderTest {

    private final PermisoManifestLoader loader = new PermisoManifestLoader(null);

    private List<Permiso> parse(String yaml) {
        return loader.parse(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)), "test");
    }

    @Test
    void parseaElManifiestoCompleto() {
        List<Permiso> permisos = parse("""
                version: 1
                permisos:
                  - clave: compras.gastos
                    modulo: compras
                    descripcion: "Acceder a gastos"
                  - aplicacion: TESORERIA
                    clave: chequeras.eliminar
                    modulo: chequeras
                    descripcion: "Eliminar chequeras"
                    activo: false
                """);

        assertThat(permisos).hasSize(2);
        assertThat(permisos.get(0).getAplicacion()).isEqualTo("TESORERIA");
        assertThat(permisos.get(0).getActivo()).isEqualTo((byte) 1);
        assertThat(permisos.get(1).getActivo()).isZero();
    }

    @Test
    void rechazaClaveFueraDeLaConvencion() {
        assertThatThrownBy(() -> parse("""
                version: 1
                permisos:
                  - clave: Compras Gastos
                    modulo: compras
                    descripcion: "x"
                """))
                .isInstanceOf(PermisoManifestException.class)
                .hasMessageContaining("clave inválida");
    }

    @Test
    void rechazaCampoDesconocido() {
        assertThatThrownBy(() -> parse("""
                version: 1
                permisos:
                  - clave: compras.gastos
                    modulo: compras
                    descripcion: "x"
                    descripcón: "typo"
                """))
                .isInstanceOf(PermisoManifestException.class)
                .hasMessageContaining("campo desconocido");
    }

    @Test
    void rechazaClaveDuplicada() {
        assertThatThrownBy(() -> parse("""
                version: 1
                permisos:
                  - clave: compras.gastos
                    modulo: compras
                    descripcion: "A"
                  - clave: compras.gastos
                    modulo: compras
                    descripcion: "B"
                """))
                .isInstanceOf(PermisoManifestException.class)
                .hasMessageContaining("duplicada");
    }

    @Test
    void exigeVersionUno() {
        assertThatThrownBy(() -> parse("""
                version: 2
                permisos: []
                """))
                .isInstanceOf(PermisoManifestException.class)
                .hasMessageContaining("version: 1");
    }

    @Test
    void elManifiestoDelRepositorioEsValido() throws Exception {
        try (InputStream input = new ClassPathResource("permisos.yaml").getInputStream()) {
            List<Permiso> permisos = loader.parse(input, "classpath:permisos.yaml");

            assertThat(permisos).isNotEmpty();
            assertThat(permisos).extracting(Permiso::getClave)
                    .contains("compras.iniciar_pedido", "permisos.administrar",
                            "chequeras.cobranzas.importar");
        }
    }
}
