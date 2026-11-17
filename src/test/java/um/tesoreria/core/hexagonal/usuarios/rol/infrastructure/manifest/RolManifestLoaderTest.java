package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.manifest;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolManifestException;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.RolSemilla;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RolManifestLoaderTest {

    private final RolManifestLoader loader = new RolManifestLoader(null);

    private List<RolSemilla> parse(String yaml) {
        return loader.parse(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)), "test");
    }

    @Test
    void parseaElManifiestoCompleto() {
        List<RolSemilla> roles = parse("""
                version: 1
                roles:
                  - nombre: OPERADOR_CHEQUERAS
                    descripcion: "Operación de chequeras"
                    permisos:
                      - "chequeras.*"
                      - "chequeras.eliminar"
                  - nombre: CONSULTA_EXTERNO
                    descripcion: "Consulta externa"
                    activo: false
                    permisos: []
                """);

        assertThat(roles).hasSize(2);
        assertThat(roles.get(0).getAplicacion()).isEqualTo("TESORERIA");
        assertThat(roles.get(0).getActivo()).isEqualTo((byte) 1);
        assertThat(roles.get(0).getPermisos())
                .containsExactly("chequeras.*", "chequeras.eliminar");
        assertThat(roles.get(1).getActivo()).isZero();
        assertThat(roles.get(1).getPermisos()).isEmpty();
    }

    @Test
    void normalizaElNombreAMayusculas() {
        List<RolSemilla> roles = parse("""
                version: 1
                roles:
                  - nombre: operador_chequeras
                    descripcion: "x"
                """);

        assertThat(roles.get(0).getNombre()).isEqualTo("OPERADOR_CHEQUERAS");
        assertThat(roles.get(0).getPermisos()).isEmpty();
    }

    @Test
    void rechazaNombreConEspacios() {
        assertThatThrownBy(() -> parse("""
                version: 1
                roles:
                  - nombre: Operador de chequeras
                    descripcion: "x"
                """))
                .isInstanceOf(RolManifestException.class)
                .hasMessageContaining("nombre inválido");
    }

    @Test
    void rechazaCampoDesconocido() {
        assertThatThrownBy(() -> parse("""
                version: 1
                roles:
                  - nombre: OPERADOR_CHEQUERAS
                    descripcion: "x"
                    descripcon: "typo"
                """))
                .isInstanceOf(RolManifestException.class)
                .hasMessageContaining("campo desconocido");
    }

    @Test
    void rechazaRolDuplicado() {
        assertThatThrownBy(() -> parse("""
                version: 1
                roles:
                  - nombre: OPERADOR_CHEQUERAS
                    descripcion: "A"
                  - nombre: OPERADOR_CHEQUERAS
                    descripcion: "B"
                """))
                .isInstanceOf(RolManifestException.class)
                .hasMessageContaining("rol duplicado");
    }

    @Test
    void rechazaPatronDePermisoInvalido() {
        assertThatThrownBy(() -> parse("""
                version: 1
                roles:
                  - nombre: OPERADOR_CHEQUERAS
                    descripcion: "x"
                    permisos:
                      - "chequeras*"
                """))
                .isInstanceOf(RolManifestException.class)
                .hasMessageContaining("permiso inválido");
    }

    @Test
    void rechazaVersionDistinta() {
        assertThatThrownBy(() -> parse("""
                version: 2
                roles: []
                """))
                .isInstanceOf(RolManifestException.class)
                .hasMessageContaining("version: 1");
    }

    @Test
    void elManifiestoRealEsValido() throws Exception {
        try (InputStream input = new ClassPathResource("roles.yaml").getInputStream()) {
            List<RolSemilla> roles = loader.parse(input, "classpath:roles.yaml");

            assertThat(roles).extracting(RolSemilla::getNombre)
                    .contains("ADMIN_TESORERIA", "OPERADOR_CHEQUERAS",
                            "OPERADOR_COMPRAS_PAGOS", "CONSULTA_EXTERNO");
            RolSemilla operador = roles.stream()
                    .filter(rol -> "OPERADOR_CHEQUERAS".equals(rol.getNombre()))
                    .findFirst()
                    .orElseThrow();
            assertThat(operador.getPermisos()).contains("chequeras.*");
        }
    }
}
