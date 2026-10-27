package um.tesoreria.core.audit;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Enmascaramiento por campo y por {@code Entidad.campo}.
 *
 * <p>El caso que motivó el cambio: {@code PermisoEntity.clave} es la clave funcional del
 * permiso ({@code compras.estimar}) y no debe ocultarse por llamarse igual que la contraseña
 * de {@code UsuarioEntity}. Lo mismo con {@code cuenta}: en las entidades contables es un
 * número de cuenta, no una cuenta bancaria.</p>
 */
class AuditMaskingTest {

    @Test
    void porDefectoLaClaveDelPermisoNoSeEnmascara() {
        AuditDiffBuilder builder = builderConDefault();

        assertThat(builder.mask("PermisoEntity", "clave", "compras.estimar"))
                .isEqualTo("compras.estimar");
    }

    @Test
    void elPasswordSeEnmascaraEnCualquierEntidad() {
        AuditDiffBuilder builder = builderConDefault();

        assertThat(builder.mask("UsuarioEntity", "password", "secreto"))
                .isEqualTo("****");
        assertThat(builder.mask("PersonaEntity", "password", "secreto"))
                .isEqualTo("****");
    }

    @Test
    void unaReglaAcotadaSoloAplicaASuEntidad() {
        AuditDiffBuilder builder = builderConReglas(List.of("Usuario.clave"));

        assertThat(builder.mask("UsuarioEntity", "clave", "secreto")).isEqualTo("****");
        assertThat(builder.mask("PermisoEntity", "clave", "compras.estimar"))
                .isEqualTo("compras.estimar");
    }

    @Test
    void laEntidadSeComparaIgnorandoMayusculasYSufijoEntity() {
        AuditDiffBuilder builder = builderConReglas(List.of("usuarioEntity.clave"));

        assertThat(builder.mask("UsuarioEntity", "clave", "secreto")).isEqualTo("****");
        assertThat(builder.mask("Usuario", "clave", "secreto")).isEqualTo("****");
    }

    @Test
    void losCamposSensiblesConservanLosUltimosCuatro() {
        AuditDiffBuilder builder = builderConReglas(List.of("Persona.documento", "cbu"));

        assertThat(builder.mask("PersonaEntity", "documento", "30123456")).isEqualTo("****3456");
        assertThat(builder.mask("BancariaEntity", "cbu", "0170099220000067891234"))
                .isEqualTo("****1234");
    }

    @Test
    void porDefectoElNumeroDeCuentaContableNoSeEnmascara() {
        AuditDiffBuilder builder = builderConDefault();

        assertThat(builder.mask("CuentaMensual", "cuenta", new java.math.BigDecimal("40100000001")))
                .isEqualTo(new java.math.BigDecimal("40100000001"));
        assertThat(builder.mask("FacultadPagoCuenta", "cuenta", new java.math.BigDecimal("40100000002")))
                .isEqualTo(new java.math.BigDecimal("40100000002"));
    }

    @Test
    void unaReglaCuentaAcotadaSoloAplicaASuEntidad() {
        AuditDiffBuilder builder = builderConReglas(List.of("Bancaria.cuenta"));

        assertThat(builder.mask("BancariaEntity", "cuenta", "0170099220000067891234"))
                .isEqualTo("****1234");
        assertThat(builder.mask("CuentaMensual", "cuenta", "40100000001"))
                .isEqualTo("40100000001");
    }

    @Test
    void elDefaultSigueEnmascarandoCbuYDocumentoEnCualquierEntidad() {
        AuditDiffBuilder builder = builderConDefault();

        assertThat(builder.mask("PersonaEntity", "documento", "30123456")).isEqualTo("****3456");
        assertThat(builder.mask("ProveedorEntity", "cbu", "0170099220000067891234"))
                .isEqualTo("****1234");
    }

    @Test
    void unaReglaVaciaNoEnmascaraNada() {
        AuditDiffBuilder builder = builderConReglas(List.of("", "  "));

        assertThat(builder.mask("UsuarioEntity", "password", "secreto")).isEqualTo("secreto");
    }

    private AuditDiffBuilder builderConDefault() {
        return new AuditDiffBuilder(new AuditProperties(), new ObjectMapper());
    }

    private AuditDiffBuilder builderConReglas(List<String> reglas) {
        AuditProperties properties = new AuditProperties();
        properties.setMaskFields(reglas);
        return new AuditDiffBuilder(properties, new ObjectMapper());
    }
}
