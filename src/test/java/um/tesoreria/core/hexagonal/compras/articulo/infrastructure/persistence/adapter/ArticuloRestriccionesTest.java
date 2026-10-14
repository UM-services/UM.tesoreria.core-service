package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.adapter;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException.ConstraintKind;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.LockTimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException.Motivo;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ReferenciaArticulo;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Excepciones SIMULADAS como las arma el dialecto MySQL de Hibernate (código, mensaje, tipo y nombre de restricción) y
 * clasificadas con la tabla de Spring para MySQL: las copias TEMPORARY del IT no tienen FK, así que 1451/1452 no se
 * pueden producir de verdad. El 1062 real está en {@code ArticuloDevDbIT}.
 */
class ArticuloRestriccionesTest {

    private static final SQLExceptionTranslator MYSQL = new SQLErrorCodeSQLExceptionTranslator("MySQL");

    @Test
    void duplicadoPrimary_idDuplicado() {
        var traducida = traducir(violacion(1062, "Duplicate entry '7' for key 'PRIMARY'", "PRIMARY"), 7L, "alta");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloConflictException.class,
                ex -> assertThat(ex.getMotivo()).isEqualTo(Motivo.ID_DUPLICADO));
    }

    @Test
    void duplicadoConNombreDeMysql8_tambienSeReconoce() {
        var traducida = traducir(violacion(1062, "Duplicate entry '7' for key 'articulos.PRIMARY'", "articulos.PRIMARY"), 7L, "alta");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloConflictException.class,
                ex -> assertThat(ex.getMotivo()).isEqualTo(Motivo.ID_DUPLICADO));
    }

    @Test
    void duplicadoDeOtraClave_conflictoGenerico() {
        var traducida = traducir(violacion(1062, "Duplicate entry 'x' for key 'otra'", "otra"), 7L, "alta");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloConflictException.class,
                ex -> assertThat(ex.getMotivo()).isEqualTo(Motivo.CONFLICTO));
    }

    @Test
    void cuentaInexistente_400ConCampo() {
        var traducida = traducir(violacion(1452,
                "Cannot add or update a child row: a foreign key constraint fails (`tesium`.`articulos`, CONSTRAINT `articulos_ibfk_1` FOREIGN KEY (`Art_Cuenta`) REFERENCES `plancta` (`Pla_Cuenta`))",
                "articulos_ibfk_1"), 7L, "alta");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloValidationException.class,
                ex -> assertThat(ex.getCampo()).isEqualTo("numeroCuenta"));
    }

    @Test
    void fkPadreDesconocida_400SinCampo() {
        var traducida = traducir(violacion(1452, "a foreign key constraint fails", "articulos_ibfk_9"), 7L, "alta");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isNull());
    }

    @Test
    void bajaConEntregas_409ConLaTabla() {
        var traducida = traducir(violacion(1451,
                "Cannot delete or update a parent row: a foreign key constraint fails (`tesium`.`entrega_detalle`, CONSTRAINT `entrega_detalle_ibfk_2` FOREIGN KEY (`NeD_Art_ID`) REFERENCES `articulos` (`Art_ID`))",
                "entrega_detalle_ibfk_2"), 7L, "baja");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloConflictException.class, ex -> {
            assertThat(ex.getMotivo()).isEqualTo(Motivo.REFERENCIADO);
            assertThat(ex.getReferencias()).containsExactly(new ReferenciaArticulo("entrega_detalle", null));
        });
    }

    @Test
    void bajaConFkDesconocida_409SinTablas() {
        var traducida = traducir(violacion(1451, "a foreign key constraint fails", "nueva_ibfk_1"), 7L, "baja");

        assertThat(traducida).isInstanceOfSatisfying(ArticuloConflictException.class, ex -> {
            assertThat(ex.getMotivo()).isEqualTo(Motivo.REFERENCIADO);
            assertThat(ex.getReferencias()).isEmpty();
        });
    }

    @Test
    void fkSinNombreExtraido_respuestaGenericaSinNpe() {
        assertThat(traducir(violacion(1452, "a foreign key constraint fails", null), 7L, "alta"))
                .isInstanceOfSatisfying(ArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isNull());
        assertThat(traducir(violacion(1451, "a foreign key constraint fails", null), 7L, "baja"))
                .isInstanceOfSatisfying(ArticuloConflictException.class, ex -> assertThat(ex.getReferencias()).isEmpty());
    }

    @Test
    void interbloqueo_bloqueadoReintentable_yEsperaVencida_bloqueadoSinReintento() {
        // Hibernate y Spring Data las entregan las dos como CannotAcquireLockException: decide el código del driver
        var interbloqueo = new CannotAcquireLockException("x",
                new LockAcquisitionException("x", new SQLException("Deadlock found", "40001", 1213)));
        var esperaVencida = new CannotAcquireLockException("x",
                new LockTimeoutException("x", new SQLException("Lock wait timeout exceeded", "HY000", 1205)));

        assertThat(traducir(interbloqueo, 7L, "edición")).isInstanceOfSatisfying(ArticuloConflictException.class, ex -> {
            assertThat(ex.getMotivo()).isEqualTo(Motivo.BLOQUEADO);
            assertThat(ex.isReintentable()).isTrue();
        });
        assertThat(traducir(esperaVencida, 7L, "bloqueo")).isInstanceOfSatisfying(ArticuloConflictException.class, ex -> {
            assertThat(ex.getMotivo()).isEqualTo(Motivo.BLOQUEADO);
            assertThat(ex.isReintentable()).isFalse();
        });
    }

    @Test
    void otroError_sigueSinTraducir() {
        var original = new DataIntegrityViolationException("x", new SQLException("Data too long", "22001", 1406));

        assertThat(traducir(original, 7L, "alta")).isSameAs(original);
    }

    @Test
    void sinSqlException_sigueSinTraducir() {
        var original = new IllegalStateException("otra cosa");

        assertThat(traducir(original, 7L, "alta")).isSameAs(original);
    }

    @Test
    void fkDesconocida_decideLaOperacionNoElCodigo() {
        // Código y operación cruzados: solo la regla por operación da estas respuestas
        assertThat(traducir(violacion(1452, "a foreign key constraint fails", "otra_fk"), 7L, ArticuloRestricciones.BAJA))
                .isInstanceOfSatisfying(ArticuloConflictException.class, ex -> {
                    assertThat(ex.getMotivo()).isEqualTo(Motivo.REFERENCIADO);
                    assertThat(ex.getReferencias()).isEmpty();
                });
        assertThat(traducir(violacion(1451, "a foreign key constraint fails", "otra_fk"), 7L, "alta"))
                .isInstanceOfSatisfying(ArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isNull());
        assertThat(traducir(violacion(1451, "a foreign key constraint fails", "otra_fk"), 7L, "edición"))
                .isInstanceOfSatisfying(ArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isNull());
    }

    @Test
    void violacionQueNoEsDeClaveForanea_sigueSinTraducir() {
        var sql = new SQLException("Column 'Art_Nombre' cannot be null", "23000", 1048);
        var original = new DataIntegrityViolationException("x", new ConstraintViolationException("x", sql, null, ConstraintKind.NOT_NULL, null));

        assertThat(traducir(original, 7L, "alta")).isSameAs(original);
    }

    private static RuntimeException traducir(RuntimeException ex, Long articuloId, String operacion) {
        return ArticuloRestricciones.traducir(MYSQL, ex, articuloId, operacion);
    }

    /** Como llega desde Spring Data o desde el flush: Hibernate envuelve la SQLException del driver. */
    private static RuntimeException violacion(int codigo, String mensaje, String restriccion) {
        var sql = new SQLException(mensaje, "23000", codigo);
        var tipo = codigo == 1062 ? ConstraintKind.UNIQUE : ConstraintKind.FOREIGN_KEY;
        return new DataIntegrityViolationException(mensaje, new ConstraintViolationException(mensaje, sql, null, tipo, restriccion));
    }

    @Test
    void textoQueLaColumnaNoAdmite_400ConElCampoDeLaColumna() {
        var sql = new SQLException("Incorrect string value: '\\xF0\\x9F\\x98\\x80' for column 'Art_Descripcion' at row 1", "HY000", 1366);
        var original = new org.hibernate.exception.GenericJDBCException("could not execute statement", sql);

        assertThat(traducir(original, 5L, "edición")).isInstanceOfSatisfying(ArticuloValidationException.class, ex -> {
            assertThat(ex.getCampo()).isEqualTo("descripcion");
            assertThat(ex.getMessage()).doesNotContain("Art_").doesNotContain("Incorrect");
        });
    }

    @Test
    void textoNoAdmitidoEnColumnaDesconocida_400SinCampo() {
        var sql = new SQLException("Incorrect string value: 'x' for column 'Otra' at row 1", "HY000", 1366);

        assertThat(traducir(new org.hibernate.exception.GenericJDBCException("x", sql), 5L, "alta"))
                .isInstanceOfSatisfying(ArticuloValidationException.class, ex -> assertThat(ex.getCampo()).isNull());
    }
}
